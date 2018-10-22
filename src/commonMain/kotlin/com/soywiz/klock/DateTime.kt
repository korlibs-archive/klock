package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Represents a Date in UTC (GMT+00) with millisecond precision.
 *
 * It is internally represented as an inlined double, thus doesn't allocate in any target including JS.
 * It can represent without loss dates between (-(2 ** 52) and (2 ** 52)):
 * - Thu Aug 10 -140744 07:15:45 GMT-0014 (Central European Summer Time)
 * - Wed May 23 144683 18:29:30 GMT+0200 (Central European Summer Time)
 */
inline class DateTime(val unixMillis: Double) : Comparable<DateTime> {
    companion object {
        val EPOCH = DateTime(0.0)

        // Can produce errors on invalid dates
        operator fun invoke(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime = DateTime(
            DateTime.dateToMillis(year, month, day) + DateTime.timeToMillis(hour, minute, second) + milliseconds
        )

        operator fun invoke(time: Long) = fromUnix(time)

        fun fromString(str: String) = SimplerDateFormat.parse(str)
        fun parse(str: String) = SimplerDateFormat.parse(str)

        fun fromUnix(time: Double): DateTime = DateTime(time)
        fun fromUnixLocal(time: Double): DateTimeWithOffset = DateTime(time).local

        fun fromUnix(time: Long): DateTime = fromUnix(time.toDouble())
        fun fromUnixLocal(time: Long): DateTimeWithOffset = fromUnixLocal(time.toDouble())

        fun now(): DateTime = DateTime(KlockInternal.currentTime)
        fun nowLocal(): DateTimeWithOffset = fromUnix(nowUnix()).local

        fun nowUnix(): Double = KlockInternal.currentTime
        fun nowUnixLong(): Long = KlockInternal.currentTime.toLong()

        // Can't produce errors on invalid dates and tries to adjust it to a valid date.
        fun createClamped(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime {
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
        fun createAdjusted(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime {
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
        fun createUnchecked(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime {
            return DateTime(
                DateTime.dateToMillisUnchecked(year, month, day) + DateTime.timeToMillisUnchecked(
                    hour,
                    minute,
                    second
                ) + milliseconds
            )
        }

        fun isLeapYear(year: Int): Boolean = Year.isLeap(year)
        fun daysInMonth(month: Int, isLeap: Boolean): Int = Month.days(month, isLeap)
        fun daysInMonth(month: Int, year: Int): Int = daysInMonth(month, isLeapYear(year))

        internal const val EPOCH_INTERNAL_MILLIS = 62135596800000.0 // Millis since 00-00-0000 00:00 UTC to UNIX EPOCH

        private const val DATE_PART_YEAR = 0
        private const val DATE_PART_DAY_OF_YEAR = 1
        private const val DATE_PART_MONTH = 2
        private const val DATE_PART_DAY = 3

        /**
         * Returns milliseconds since EPOCH.
         */
        internal fun dateToMillisUnchecked(year: Int, month: Int, day: Int): Double {
            val y = (year - 1)
            val n = y * 365 + y / 4 - y / 100 + y / 400 + Month.daysToStart(month, year) + day - 1
            return n.toDouble() * MILLIS_PER_DAY.toDouble() - EPOCH_INTERNAL_MILLIS
        }

        internal fun timeToMillisUnchecked(hour: Int, minute: Int, second: Int): Double {
            return (hour.toDouble() * 3600 + minute.toDouble() * 60 + second.toDouble()) * MILLIS_PER_SECOND
        }

        internal fun dateToMillis(year: Int, month: Int, day: Int): Double {
            //Year.checked(year)
            Month.check(month)
            if (day !in 1..Month.days(month, year)) {
                throw DateException("Day $day not valid for year=$year and month=$month")
            }
            return dateToMillisUnchecked(year, month, day)
        }

        internal fun timeToMillis(hour: Int, minute: Int, second: Int): Double {
            if (hour !in 0..23) throw DateException("Hour $hour not in 0..23")
            if (minute !in 0..59) throw DateException("Minute $minute not in 0..59")
            if (second !in 0..59) throw DateException("Second $second not in 0..59")
            return timeToMillisUnchecked(hour, minute, second)
        }

        /**
         * [millis] are 00-00-0000 based.
         */
        internal fun getDatePart(millis: Double, part: Int): Int {
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

    private val internalMillis get() = EPOCH_INTERNAL_MILLIS + unixMillis

    private fun getDatePart(part: Int): Int = getDatePart(internalMillis, part)

    val offset: Int get() = 0
    val utc: DateTime get() = this
    val adjusted: DateTime get() = this
    val unixDouble: Double get() = unixMillis
    val year: Int get() = getDatePart(DATE_PART_YEAR)
    val month1: Int get() = getDatePart(DATE_PART_MONTH)
    val dayOfMonth: Int get() = getDatePart(DATE_PART_DAY)
    val dayOfWeekInt: Int get() = ((internalMillis / MILLIS_PER_DAY + 1) % 7).toInt()
    val dayOfYear: Int get() = getDatePart(DATE_PART_DAY_OF_YEAR)
    val hours: Int get() = (((internalMillis / MILLIS_PER_HOUR) % 24).toInt())
    val minutes: Int get() = ((internalMillis / MILLIS_PER_MINUTE) % 60).toInt()
    val seconds: Int get() = ((internalMillis / MILLIS_PER_SECOND) % 60).toInt()
    val milliseconds: Int get() = ((internalMillis) % 1000).toInt()
    val timeZone: String get() = "UTC"

    val unixLong: Long get() = unixDouble.toLong()
    val dayOfWeek: DayOfWeek get() = DayOfWeek[dayOfWeekInt]
    val month0: Int get() = month1 - 1
    val month: Month get() = Month[month1]
    val local get() = DateTimeWithOffset(this, Klock.localTimezoneOffset(DateTime(unixDouble)).minutes.toInt())

    @Deprecated("", ReplaceWith("utc"))
    fun toUtc(): DateTime = utc

    @Deprecated("", ReplaceWith("local"))
    fun toLocal() = local

    fun addOffset(offset: Int) = DateTimeWithOffset(this, this.offset + offset)
    fun toOffset(offset: Int) = DateTimeWithOffset(this, offset)

    operator fun plus(delta: DateSpan): DateTime = this.add(delta.totalMonths, 0.0)
    operator fun plus(delta: DateTimeSpan): DateTime = this.add(delta.totalMonths, delta.totalMilliseconds)
    operator fun plus(delta: TimeSpan): DateTime = add(0, delta.milliseconds)

    operator fun minus(delta: DateSpan): DateTime = this + -delta
    operator fun minus(delta: DateTimeSpan): DateTime = this + -delta
    operator fun minus(delta: TimeSpan): DateTime = this + (-delta)

    operator fun minus(other: DateTime): TimeSpan = (this.unixDouble - other.unixDouble).milliseconds

    fun toString(format: String): String = toString(SimplerDateFormat(format))
    fun toString(format: SimplerDateFormat): String = format.format(this)

    fun add(deltaMonths: Int, deltaMilliseconds: Double): DateTime = when {
        deltaMonths == 0 && deltaMilliseconds == 0.0 -> this
        deltaMonths == 0 -> DateTime(this.unixMillis + deltaMilliseconds)
        else -> {
            var year = this.year
            var month = this.month.index
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

            DateTime(dateToMillisUnchecked(year, month, day) + (internalMillis % MILLIS_PER_DAY) + deltaMilliseconds)
        }
    }

    fun format(dt: SimplerDateFormat): String = dt.format(this)
    fun format(dt: String): String = SimplerDateFormat(dt).format(this)

    override fun compareTo(other: DateTime): Int = this.adjusted.unixMillis.compareTo(other.adjusted.unixMillis)
    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
