package com.soywiz.klock

import kotlin.math.abs

enum class DayOfWeek(val index: Int) {
	Sunday(0), Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6);

	companion object {
		val BY_INDEX = values()
		operator fun get(index: Int) = BY_INDEX[index]
	}
}

data class Year(val year: Int) : Comparable<Year> {
	companion object {
		fun checked(year: Int) = year.apply { if (year !in 1..9999) throw DateException("Year $year not in 1..9999") }
		fun isLeapChecked(year: Int): Boolean = isLeap(checked(year))
		fun isLeap(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
	}

	override fun compareTo(other: Year): Int = this.year.compareTo(other.year)
}

enum class Month(val index: Int) {
	January(1), // 31
	February(2), // 28/29
	March(3), // 31
	April(4), // 30
	May(5), // 31
	June(6), // 30
	July(7), // 31
	August(8), // 31
	September(9), // 30
	October(10), // 31
	November(11), // 30
	December(12); // 31

	val index0: Int get() = index - 1

	fun days(isLeap: Boolean): Int = days(index, isLeap)
	fun daysToStart(isLeap: Boolean): Int = daysToStart(index, isLeap)
	fun daysToEnd(isLeap: Boolean): Int = daysToEnd(index, isLeap)

	fun days(year: Int): Int = days(index, year)
	fun daysToStart(year: Int): Int = daysToStart(index, year)
	fun daysToEnd(year: Int): Int = daysToEnd(index, year)

	companion object {
		val BY_INDEX0 = values()
		operator fun invoke(index1: Int) = BY_INDEX0[(index1 - 1) umod 12]
		operator fun get(index1: Int) = BY_INDEX0[(index1 - 1) umod 12]

		fun check(month: Int): Int {
			if (month !in 1..12) throw DateException("Month $month not in 1..12")
			return month
		}

		fun normalize(month: Int) = ((month - 1) umod 12) + 1

		fun days(month: Int, isLeap: Boolean): Int {
			//val nmonth = check(month)
			val nmonth = normalize(month)
			val days = DAYS_TO_MONTH(isLeap)
			return days[nmonth] - days[nmonth - 1]
		}

		fun daysToStart(month: Int, isLeap: Boolean): Int = DAYS_TO_MONTH(isLeap)[(month - 1) umod 13]
		fun daysToEnd(month: Int, isLeap: Boolean): Int = DAYS_TO_MONTH(isLeap)[month umod 13]

		fun days(month: Int, year: Int): Int = days(month, Year.isLeap(year))
		fun daysToStart(month: Int, year: Int): Int = daysToStart(month, Year.isLeap(year))
		fun daysToEnd(month: Int, year: Int): Int = daysToEnd(month, Year.isLeap(year))

		fun DAYS_TO_MONTH(isLeap: Boolean): IntArray = if (isLeap) DAYS_TO_MONTH_366 else DAYS_TO_MONTH_365

		val DAYS_TO_MONTH_366 = intArrayOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366)
		val DAYS_TO_MONTH_365 = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365)
	}
}

class DateException(msg: String) : RuntimeException(msg)

private const val MILLIS_PER_SECOND = 1000
private const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60
private const val MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60
private const val MILLIS_PER_DAY = MILLIS_PER_HOUR * 24
private const val MILLIS_PER_WEEK = MILLIS_PER_DAY * 7

private const val DAYS_PER_YEAR = 365
private const val DAYS_PER_4_YEARS = DAYS_PER_YEAR * 4 + 1
private const val DAYS_PER_100_YEARS = DAYS_PER_4_YEARS * 25 - 1
private const val DAYS_PER_400_YEARS = DAYS_PER_100_YEARS * 4 + 1

interface DateTime : Comparable<DateTime> {
	val year: Int
	val month: Int
	val dayOfWeekInt: Int
	val dayOfMonth: Int
	val dayOfYear: Int
	val hours: Int
	val minutes: Int
	val seconds: Int
	val milliseconds: Int
	val timeZone: String
	val unix: Long
	val offset: Int
	val utc: UtcDateTime
	fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime

	val dayOfWeek: DayOfWeek get() = DayOfWeek[dayOfWeekInt]
	val month0: Int get() = month - 1
	val month1: Int get() = month
	val monthEnum: Month get() = Month[month1]

	fun toUtc(): DateTime = utc
	fun toLocal() = OffsetDateTime(this, Klock.getLocalTimezoneOffset(unix))
	fun addOffset(offset: Int) = OffsetDateTime(this, this.offset + offset)
	fun toOffset(offset: Int) = OffsetDateTime(this, offset)
	fun addYears(delta: Int): DateTime = add(delta * 12, 0L)
	fun addMonths(delta: Int): DateTime = add(delta, 0L)
	fun addDays(delta: Double): DateTime = add(0, (delta * MILLIS_PER_DAY).toLong())
	fun addWeeks(delta: Double): DateTime = add(0, (delta * MILLIS_PER_WEEK).toLong())
	fun addHours(delta: Double): DateTime = add(0, (delta * MILLIS_PER_HOUR).toLong())
	fun addMinutes(delta: Double): DateTime = add(0, (delta * MILLIS_PER_MINUTE).toLong())
	fun addSeconds(delta: Double): DateTime = add(0, (delta * MILLIS_PER_SECOND).toLong())
	fun addMilliseconds(delta: Double): DateTime = if (delta == 0.0) this else add(0, delta.toLong())
	fun addMilliseconds(delta: Long): DateTime = if (delta == 0L) this else add(0, delta)

	operator fun plus(delta: TimeDistance): DateTime = this.add(
		delta.totalMonths,
		delta.totalMilliseconds
	)
    operator fun plus(delta: TimeSpan): DateTime = addMilliseconds(delta.milliseconds.toDouble())
	operator fun minus(delta: TimeDistance): DateTime = this + -delta
    operator fun minus(delta: TimeSpan): DateTime = addMilliseconds(-delta.milliseconds.toDouble())
	fun toString(format: String): String = toString(SimplerDateFormat(format))
	fun toString(format: SimplerDateFormat): String = format.format(this)

	override fun hashCode(): Int
	override fun equals(other: Any?): Boolean

	companion object {
		val EPOCH by lazy { DateTime(1970, 1, 1, 0, 0, 0) as UtcDateTime }
		internal val EPOCH_INTERNAL_MILLIS by lazy { EPOCH.internalMillis }

		// Can produce errors on invalid dates
		operator fun invoke(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime {
			return UtcDateTime(UtcDateTime.dateToMillis(year, month, day) + UtcDateTime.timeToMillis(hour, minute, second) + milliseconds, true)
		}

		operator fun invoke(time: Long) = fromUnix(time)

		fun fromString(str: String) = SimplerDateFormat.parse(str)
		fun parse(str: String) = SimplerDateFormat.parse(str)

		fun fromUnix(time: Long): DateTime = UtcDateTime(EPOCH_INTERNAL_MILLIS + time, true)
		fun fromUnixLocal(time: Long): DateTime = UtcDateTime(EPOCH_INTERNAL_MILLIS + time, true).toLocal()

		fun nowUnix() = Klock.currentTimeMillis()
		fun now() = fromUnix(nowUnix())
		fun nowLocal() = fromUnix(nowUnix()).toLocal()

		// Can't produce errors on invalid dates and tries to adjust it to a valid date.
		fun createClamped(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime {
			val clampedMonth = month.clamp(1, 12)
			return createUnchecked(
				year = year,
				month = clampedMonth,
				day = day.clamp(1, daysInMonth(clampedMonth, year)),
				hour = hour.clamp(0, 23),
				minute = minute.clamp(0, 59),
				second = second.clamp(0, 59),
				milliseconds = milliseconds
			)
		}

		// Can't produce errors on invalid dates and tries to adjust it to a valid date.
		fun createAdjusted(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime {
			var dy = year
			var dm = month
			var dd = day
			var th = hour
			var tm = minute
			var ts = second

			tm += ts.cycleSteps(0, 59); ts = ts.cycle(0, 59) // Adjust seconds, adding minutes
			th += tm.cycleSteps(0, 59); tm = tm.cycle(0, 59) // Adjust minutes, adding hours
			dd += th.cycleSteps(0, 23); th = th.cycle(0, 23) // Adjust hours, adding days

			while (true) {
				val dup = daysInMonth(dm, dy)

				dm += dd.cycleSteps(1, dup); dd = dd.cycle(1, dup) // Adjust days, adding months
				dy += dm.cycleSteps(1, 12); dm = dm.cycle(1, 12) // Adjust months, adding years

				// We already have found a day that is valid for the adjusted month!
				if (dd.cycle(1, daysInMonth(dm, dy)) == dd) {
					break
				}
			}

			return createUnchecked(dy, dm, dd, th, tm, ts, milliseconds)
		}

		// Can't produce errors on invalid dates
		fun createUnchecked(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime {
			return UtcDateTime(UtcDateTime.dateToMillisUnchecked(year, month, day) + UtcDateTime.timeToMillisUnchecked(hour, minute, second) + milliseconds, true)
		}

		fun isLeapYear(year: Int): Boolean = Year.isLeap(year)
		fun daysInMonth(month: Int, isLeap: Boolean): Int = Month.days(month, isLeap)
		fun daysInMonth(month: Int, year: Int): Int = daysInMonth(month, isLeapYear(year))
	}
}

class OffsetDateTime private constructor(
	override val utc: UtcDateTime,
	override val offset: Int,
	val adjusted: DateTime = utc.addMinutes(offset.toDouble())
) : DateTime by adjusted {
	private val deltaTotalMinutesAbs: Int = abs(offset)
	val positive: Boolean get() = offset >= 0
	val deltaHoursAbs: Int get() = deltaTotalMinutesAbs / 60
	val deltaMinutesAbs: Int get() = deltaTotalMinutesAbs % 60

	companion object {
		//operator fun invoke(utc: DateTime, offset: Int) = OffsetDateTime(utc.utc, utc.offsetTotalMinutes + offset)
		operator fun invoke(utc: DateTime, offset: Int) = OffsetDateTime(utc.utc, offset)
	}

	override val timeZone: String = "GMT%s%02d%02d".format(
		if (positive) "+" else "-",
		deltaHoursAbs,
		deltaMinutesAbs
	)

	override fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime =
		OffsetDateTime(utc.add(deltaMonths, deltaMilliseconds), offset)

	override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}

class UtcDateTime internal constructor(internal val internalMillis: Long, @Suppress("UNUSED_PARAMETER") dummy: Boolean) : DateTime {
	companion object {
		private const val DATE_PART_YEAR = 0
		private const val DATE_PART_DAY_OF_YEAR = 1
		private const val DATE_PART_MONTH = 2
		private const val DATE_PART_DAY = 3

		internal fun dateToMillisUnchecked(year: Int, month: Int, day: Int): Long {
			val y = year - 1
			Month.daysToStart(month, year)
			val n = y * 365 + y / 4 - y / 100 + y / 400 + Month.daysToStart(month, year) + day - 1
			return n.toLong() * MILLIS_PER_DAY.toLong()
		}

		internal fun timeToMillisUnchecked(hour: Int, minute: Int, second: Int): Long {
			return (hour.toLong() * 3600 + minute.toLong() * 60 + second.toLong()) * MILLIS_PER_SECOND
		}

		internal fun dateToMillis(year: Int, month: Int, day: Int): Long {
			//Year.checked(year)
			Month.check(month)
			if (day !in 1..Month.days(month, year)) throw DateException("Day $day not valid for year=$year and month=$month")
			return dateToMillisUnchecked(year, month, day)
		}

		internal fun timeToMillis(hour: Int, minute: Int, second: Int): Long {
			if (hour !in 0..23) throw DateException("Hour $hour not in 0..23")
			if (minute !in 0..59) throw DateException("Minute $minute not in 0..59")
			if (second !in 0..59) throw DateException("Second $second not in 0..59")
			return timeToMillisUnchecked(hour, minute, second)
		}

		internal fun getDatePart(millis: Long, part: Int): Int {
			var n = (millis / MILLIS_PER_DAY).toInt()
			val y400 = n / DAYS_PER_400_YEARS
			n -= y400 * DAYS_PER_400_YEARS
			var y100 = n / DAYS_PER_100_YEARS
			if (y100 == 4) y100 = 3
			n -= y100 * DAYS_PER_100_YEARS
			val y4 = n / DAYS_PER_4_YEARS
			n -= y4 * DAYS_PER_4_YEARS
			var y1 = n / DAYS_PER_YEAR
			if (y1 == 4) y1 = 3
			if (part == DATE_PART_YEAR) return y400 * 400 + y100 * 100 + y4 * 4 + y1 + 1
			n -= y1 * DAYS_PER_YEAR
			if (part == DATE_PART_DAY_OF_YEAR) return n + 1
			val leapYear = y1 == 3 && (y4 != 24 || y100 == 3)
			var m = n shr 5 + 1
			while (n >= Month.daysToEnd(m, leapYear)) m++
			return if (part == DATE_PART_MONTH) m else n - Month.daysToStart(m, leapYear) + 1
		}
	}

	private fun getDatePart(part: Int): Int = Companion.getDatePart(internalMillis, part)

	override val offset: Int = 0
	override val utc: UtcDateTime = this
	override val unix: Long get() = (internalMillis - DateTime.EPOCH.internalMillis)
	override val year: Int get() = getDatePart(DATE_PART_YEAR)
	override val month: Int get() = getDatePart(DATE_PART_MONTH)
	override val dayOfMonth: Int get() = getDatePart(DATE_PART_DAY)
	override val dayOfWeekInt: Int get() = ((internalMillis / MILLIS_PER_DAY + 1) % 7).toInt()
	override val dayOfYear: Int get() = getDatePart(DATE_PART_DAY_OF_YEAR)
	override val hours: Int get() = (((internalMillis / MILLIS_PER_HOUR) % 24).toInt())
	override val minutes: Int get() = ((internalMillis / MILLIS_PER_MINUTE) % 60).toInt()
	override val seconds: Int get() = ((internalMillis / MILLIS_PER_SECOND) % 60).toInt()
	override val milliseconds: Int get() = ((internalMillis) % 1000).toInt()
	override val timeZone: String get() = "UTC"

	override fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime = when {
		deltaMonths == 0 && deltaMilliseconds == 0L -> this
		deltaMonths == 0 -> UtcDateTime(this.internalMillis + deltaMilliseconds, true)
		else -> {
			var year = this.year
			var month = this.month
			var day = this.dayOfMonth
			val i = month - 1 + deltaMonths

            if (i >= 0) {
				month = i % 12 + 1
				year += i / 12
			} else {
				month = 12 + (i + 1) % 12
				year += (i - 11) / 12
			}
			//Year.checked(year)
			val days = Month.days(month, year)
			if (day > days) day = days

			UtcDateTime(dateToMillisUnchecked(year, month, day) + (internalMillis % MILLIS_PER_DAY) + deltaMilliseconds, true)
		}
	}

	override operator fun compareTo(other: DateTime): Int = this.unix.compareTo(other.unix)
	override fun hashCode(): Int = internalMillis.hashCode()
	override fun equals(other: Any?): Boolean = this.unix == (other as? DateTime?)?.unix
	override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}

data class TimeDistance(val years: Int = 0, val months: Int = 0, val days: Double = 0.0, val hours: Double = 0.0, val minutes: Double = 0.0, val seconds: Double = 0.0, val milliseconds: Double = 0.0) : Comparable<TimeDistance> {
	operator fun unaryMinus() = TimeDistance(-years, -months, -days, -hours, -minutes, -seconds, -milliseconds)

	operator fun minus(other: TimeDistance) = this + -other

	operator fun plus(other: TimeDistance) = TimeDistance(
		years + other.years,
		months + other.months,
		days + other.days,
		hours + other.hours,
		minutes + other.minutes,
		seconds + other.seconds,
		milliseconds + other.milliseconds
	)

	@Suppress("NOTHING_TO_INLINE")
	inline operator fun times(times: Number) = times(times.toDouble())

	operator fun times(times: Double) = TimeDistance(
		(years * times).toInt(),
		(months * times).toInt(),
		days * times,
		hours * times,
		minutes * times,
		seconds * times,
		milliseconds * times
	)

	val totalMonths get() = years * 12 + months
	val totalMilliseconds: Long by lazy { (days * MILLIS_PER_DAY + hours * MILLIS_PER_HOUR + minutes * MILLIS_PER_MINUTE + seconds * MILLIS_PER_SECOND + milliseconds).toLong() }

	// @TODO: If milliseconds overflow months this could not be exactly true. But probably will work in most cases.
	override fun compareTo(other: TimeDistance): Int {
		if (this.totalMonths != other.totalMonths) return this.totalMonths.compareTo(other.totalMonths)
		return this.totalMilliseconds.compareTo(other.totalMilliseconds)
	}
}

inline val Int.years get() = TimeDistance(years = this)
inline val Int.months get() = TimeDistance(months = this)
inline val Number.days get() = TimeDistance(days = this.toDouble())
inline val Number.weeks get() = TimeDistance(days = this.toDouble() * 7)
inline val Number.hours get() = TimeDistance(hours = this.toDouble())
inline val Number.minutes get() = TimeDistance(minutes = this.toDouble())

@Suppress("DataClassPrivateConstructor")
data class TimeSpan private constructor(val ms: Int) : Comparable<TimeSpan> {
	val milliseconds: Int get() = this.ms
	val seconds: Double get() = this.ms.toDouble() / 1000.0

	companion object {
		val ZERO = TimeSpan(0)
		@PublishedApi internal fun fromMilliseconds(ms: Int) = when (ms) {
			0 -> ZERO
			else -> TimeSpan(ms)
		}

		private val timeSteps = listOf(60, 60, 24)
		private fun toTimeStringRaw(totalMilliseconds: Int, components: Int = 3): String {
			var timeUnit = totalMilliseconds / 1000

			val out = arrayListOf<String>()

			for (n in 0 until components) {
				if (n == components - 1) {
					out += "%02d".format(timeUnit)
					break
				}
				val step = timeSteps.getOrNull(n) ?: throw RuntimeException("Just supported ${timeSteps.size} steps")
				val cunit = timeUnit % step
				timeUnit /= step
				out += "%02d".format(cunit)
			}

			return out.reversed().joinToString(":")
		}

		fun toTimeString(totalMilliseconds: Int, components: Int = 3, addMilliseconds: Boolean = false): String {
			val milliseconds = totalMilliseconds % 1000
			val out = toTimeStringRaw(totalMilliseconds, components)
			return if (addMilliseconds) "$out.$milliseconds" else out
		}
	}

	override fun compareTo(other: TimeSpan): Int = this.ms.compareTo(other.ms)

	operator fun plus(other: TimeSpan): TimeSpan = TimeSpan(this.ms + other.ms)
	operator fun minus(other: TimeSpan): TimeSpan = TimeSpan(this.ms - other.ms)
	operator fun times(scale: Int): TimeSpan = TimeSpan(this.ms * scale)
	operator fun times(scale: Double): TimeSpan = TimeSpan((this.ms * scale).toInt())

	fun toTimeString(components: Int = 3, addMilliseconds: Boolean = false): String = toTimeString(milliseconds, components, addMilliseconds)
}

inline val Number.milliseconds get() = TimeSpan.fromMilliseconds(this.toInt())
inline val Number.seconds get() = TimeSpan.fromMilliseconds((this.toDouble() * 1000.0).toInt())

class SimplerDateFormat(val format: String) {
	companion object {
		private val rx = Regex("('[\\w]+'|[\\w]+)")
		private val englishDaysOfWeek = listOf(
			"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
		)
		private val englishMonths = listOf(
			"january", "february", "march", "april", "may", "june",
			"july", "august", "september", "october", "november", "december"
		)
		private val englishMonths3 = englishMonths.map { it.substr(0, 3) }

		val DEFAULT_FORMAT by lazy { SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z") }
		val FORMAT1 by lazy { SimplerDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") }

		val FORMATS = listOf(DEFAULT_FORMAT, FORMAT1)

		fun parse(str: String): DateTime {
			var lastError: Throwable? = null
			for (format in FORMATS) {
				try {
					return format.parseDate(str)
				} catch (e: Throwable) {
					lastError = e
				}
			}
			throw lastError!!
		}
	}

	private val parts = arrayListOf<String>()
	//val escapedFormat = Regex.escape(format)
	private val escapedFormat = Regex.escapeReplacement(format)

	private val rx2: Regex = Regex("^" + escapedFormat.replace(rx) { result ->
		val v = result.groupValues[0]
		parts += v
		if (v.startsWith("'")) {
			"(" + Regex.escapeReplacement(v.trim('\'')) + ")"
		} else {
			"([\\w\\+\\-]+)"
		}
	} + "$")

	private val parts2 = escapedFormat.splitKeep(rx)

	// EEE, dd MMM yyyy HH:mm:ss z -- > Sun, 06 Nov 1994 08:49:37 GMT
	// YYYY-MM-dd HH:mm:ss

	fun format(date: Long): String = format(DateTime.fromUnix(date))

	fun format(dd: DateTime): String {
		var out = ""
		for (name2 in parts2) {
			val name = name2.trim('\'')
			out += when (name) {
				"EEE" -> englishDaysOfWeek[dd.dayOfWeek.index].substr(0, 3).capitalize()
				"EEEE" -> englishDaysOfWeek[dd.dayOfWeek.index].capitalize()
				"z", "zzz" -> dd.timeZone
				"d" -> "%d".format(dd.dayOfMonth)
				"dd" -> "%02d".format(dd.dayOfMonth)
				"MM" -> "%02d".format(dd.month1)
				"MMM" -> englishMonths[dd.month0].substr(0, 3).capitalize()
				"yyyy" -> "%04d".format(dd.year)
				"YYYY" -> "%04d".format(dd.year)
				"HH" -> "%02d".format(dd.hours)
				"mm" -> "%02d".format(dd.minutes)
				"ss" -> "%02d".format(dd.seconds)
				else -> name
			}
		}
		return out
	}

	fun parse(str: String): Long = parseDate(str).unix

	fun parseDate(str: String): DateTime {
		return tryParseDate(str) ?: throw RuntimeException("Not a valid format: '$str' for '$format'")
	}

	fun tryParseDate(str: String): DateTime? {
		var second = 0
		var minute = 0
		var hour = 0
		var day = 1
		var month = 1
		var fullYear = 1970
		val result = rx2.find(str) ?: return null
		for ((name, value) in parts.zip(result.groupValues.drop(1))) {
			when (name) {
				"EEE", "EEEE" -> Unit // day of week (Sun | Sunday)
				"z", "zzz" -> Unit // timezone (GMT)
				"d", "dd" -> day = value.toInt()
				"MM" -> month = value.toInt()
				"MMM" -> month = englishMonths3.indexOf(value.toLowerCase()) + 1
				"yyyy", "YYYY" -> fullYear = value.toInt()
				"HH" -> hour = value.toInt()
				"mm" -> minute = value.toInt()
				"ss" -> second = value.toInt()
				else -> {
					// ...
				}
			}
		}
		//return DateTime.createClamped(fullYear, month, day, hour, minute, second)
		return DateTime.createAdjusted(fullYear, month, day, hour, minute, second)
	}
}

private val formatRegex = Regex("%([-]?\\d+)?(\\w)")

private fun String.format(vararg params: Any): String {
	var paramIndex = 0
	return formatRegex.replace(this) { mr ->
		val param = params[paramIndex++]
		//println("param: $param")
		val size = mr.groupValues[1]
		val type = mr.groupValues[2]
		val str = when (type) {
			"d" -> (param as Number).toLong().toString()
			"X" -> (param as Number).toLong().toString(16).toUpperCase()
			"x" -> (param as Number).toLong().toString(16).toLowerCase()
			else -> "$param"
		}
		val prefix = if (size.startsWith('0')) '0' else ' '
		val asize = size.toIntOrNull()
		var str2 = str
		if (asize != null) {
			while (str2.length < asize) {
				str2 = prefix + str2
			}
		}
		str2
	}
}

private val DIGITS_UPPER = "0123456789ABCDEF"

private fun Long.toString(radix: Int): String {
	val isNegative = this < 0
	var temp = abs(this)
	if (temp == 0L) {
		return "0"
	} else {
		var out = ""
		while (temp != 0L) {
			val digit = temp % radix
			temp /= radix
			out += DIGITS_UPPER[digit.toInt()]
		}
		val rout = out.reversed()
		return if (isNegative) "-$rout" else rout
	}
}

private fun String.substr(start: Int): String = this.substr(start, this.length)

private fun String.substr(start: Int, length: Int): String {
	val low = (if (start >= 0) start else this.length + start).clamp(0, this.length)
	val high = (if (length >= 0) low + length else this.length + length).clamp(0, this.length)
	if (high < low) {
		return ""
	} else {
		return this.substring(low, high)
	}
}

private fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this

private fun Int.cycle(min: Int, max: Int): Int {
	return ((this - min) umod (max - min + 1)) + min
}

private fun Int.cycleSteps(min: Int, max: Int): Int {
	return (this - min) / (max - min + 1)
}

private fun String.splitKeep(regex: Regex): List<String> {
	val str = this
	val out = arrayListOf<String>()
	var lastPos = 0
	for (part in regex.findAll(this)) {
		val prange = part.range
		if (lastPos != prange.start) {
			out += str.substring(lastPos, prange.start)
		}
		out += str.substring(prange)
		lastPos = prange.endInclusive + 1
	}
	if (lastPos != str.length) {
		out += str.substring(lastPos)
	}
	return out
}

private infix fun Int.umod(that: Int): Int {
	val remainder = this % that
	return when {
		remainder < 0 -> remainder + that
		else -> remainder
	}
}
