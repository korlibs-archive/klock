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
            month: Month,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime = DateTime(
            DateTime.dateToMillis(year, month.index1, day) + DateTime.timeToMillis(hour, minute, second) + milliseconds
        )

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
        fun fromUnix(time: Long): DateTime = fromUnix(time.toDouble())

        fun now(): DateTime = DateTime(KlockInternal.currentTime)
        fun nowLocal(): DateTimeWithOffset = DateTimeWithOffset.nowLocal()
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
        fun daysInMonth(month: Int, isLeap: Boolean): Int = Month(month).days(isLeap)
        fun daysInMonth(month: Int, year: Int): Int = daysInMonth(month, isLeapYear(year))

        internal const val EPOCH_INTERNAL_MILLIS = 62135596800000.0 // Millis since 00-00-0000 00:00 UTC to UNIX EPOCH

        internal enum class DatePart { Year, DayOfYear, Month, Day}

        /**
         * Returns milliseconds since EPOCH.
         */
        internal fun dateToMillisUnchecked(year: Int, month: Int, day: Int): Double =
            (Year(year).daysSinceOne + Month(month).daysToStart(year) + day - 1) * MILLIS_PER_DAY.toDouble() - EPOCH_INTERNAL_MILLIS

        private fun timeToMillisUnchecked(hour: Int, minute: Int, second: Int): Double =
            hour.toDouble() * MILLIS_PER_HOUR + minute.toDouble() * MILLIS_PER_MINUTE + second.toDouble() * MILLIS_PER_SECOND

        private fun dateToMillis(year: Int, month: Int, day: Int): Double {
            //Year.checked(year)
            Month.checked(month)
            if (day !in 1..Month(month).days(year)) throw DateException("Day $day not valid for year=$year and month=$month")
            return dateToMillisUnchecked(year, month, day)
        }

        private fun timeToMillis(hour: Int, minute: Int, second: Int): Double {
            if (hour !in 0..23) throw DateException("Hour $hour not in 0..23")
            if (minute !in 0..59) throw DateException("Minute $minute not in 0..59")
            if (second !in 0..59) throw DateException("Second $second not in 0..59")
            return timeToMillisUnchecked(hour, minute, second)
        }

        /**
         * [millis] are 00-00-0000 based.
         */
        internal fun getDatePart(millis: Double, part: DatePart): Int {
            val totalDays = (millis / MILLIS_PER_DAY).toInt()

            // Year
            val year = Year.fromDays(totalDays)
            if (part == DatePart.Year) return year.year

            // Day of Year
            val isLeap = year.isLeap
            val startYearDays = year.daysSinceOne
            val dayOfYear = 1 + (totalDays - startYearDays)
            if (part == DatePart.DayOfYear) return dayOfYear

            // Month
            val month = Month.fromDayOfYear(dayOfYear, isLeap) ?: error("Invalid dayOfYear=$dayOfYear, isLeap=$isLeap")
            if (part == DatePart.Month) return month.index1

            // Day
            val dayOfMonth = dayOfYear - month.daysToStart(isLeap)
            if (part == DatePart.Day) return dayOfMonth

            error("Invalid DATE_PART")
        }
    }

    val zeroMillis: Double get() = EPOCH_INTERNAL_MILLIS + unixMillis
    val localOffset: TimeSpan get() = Klock.localTimezoneOffset(DateTime(unixDouble))

    val unixDouble: Double get() = unixMillis
    val year: Int get() = getDatePart(zeroMillis, DatePart.Year)
    val month1: Int get() = getDatePart(zeroMillis, DatePart.Month)
    val dayOfMonth: Int get() = getDatePart(zeroMillis, DatePart.Day)
    val dayOfWeekInt: Int get() = ((zeroMillis / MILLIS_PER_DAY + 1) % 7).toInt()
    val dayOfYear: Int get() = getDatePart(zeroMillis, DatePart.DayOfYear)
    val hours: Int get() = (((zeroMillis / MILLIS_PER_HOUR) % 24).toInt())
    val minutes: Int get() = ((zeroMillis / MILLIS_PER_MINUTE) % 60).toInt()
    val seconds: Int get() = ((zeroMillis / MILLIS_PER_SECOND) % 60).toInt()
    val milliseconds: Int get() = ((zeroMillis) % 1000).toInt()

    val unixLong: Long get() = unixDouble.toLong()
    val dayOfWeek: DayOfWeek get() = DayOfWeek[dayOfWeekInt]
    val month0: Int get() = month1 - 1
    val month: Month get() = Month[month1]

    val localBase get() = DateTimeWithOffset(this, localOffset)
    val localAdjusted get() = DateTimeWithOffset.adjusted(this, localOffset)

    @Deprecated("", ReplaceWith("local"))
    fun toLocal() = localBase

    fun toOffsetBase(offset: TimeSpan) = DateTimeWithOffset(this, offset)
    fun toOffsetBase(minutes: Int) = toOffsetBase(minutes.minutes)

    fun toOffsetAdjusted(offset: TimeSpan) = DateTimeWithOffset.adjusted(this, offset)
    fun toOffsetAdjusted(minutes: Int) = toOffsetAdjusted(minutes.minutes)

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
            var month = this.month.index1
            var day = this.dayOfMonth
            val i = month - 1 + deltaMonths

            if (i >= 0) {
                month = i % Month.Count + 1
                year += i / Month.Count
            } else {
                month = Month.Count + (i + 1) % Month.Count
                year += (i - (Month.Count - 1)) / Month.Count
            }
            //Year.checked(year)
            val days = Month(month).days(year)
            if (day > days) day = days

            DateTime(dateToMillisUnchecked(year, month, day) + (zeroMillis % MILLIS_PER_DAY) + deltaMilliseconds)
        }
    }

    fun add(date: DateSpan, time: TimeSpan): DateTime = add(date.totalMonths, time.milliseconds)

    fun format(dt: SimplerDateFormat): String = dt.format(this)
    fun format(dt: String): String = SimplerDateFormat(dt).format(this)

    override fun compareTo(other: DateTime): Int = this.unixMillis.compareTo(other.unixMillis)
    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
