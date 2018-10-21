package com.soywiz.klock

import com.soywiz.klock.internal.*

interface DateTime : Comparable<DateTime> {
    val year: Int
    val month1: Int
    val dayOfWeekInt: Int
    val dayOfMonth: Int
    val dayOfYear: Int
    val hours: Int
    val minutes: Int
    val seconds: Int
    val milliseconds: Int
    val timeZone: String
    val unix: Long
    val unixDouble: Double get() = unix.toDouble()
    val offset: Int
    fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime

    val dayOfWeek: DayOfWeek get() = DayOfWeek[dayOfWeekInt]
    val month0: Int get() = month1 - 1
    val month: Month get() = Month[month1]

    val utc: UtcDateTime
    val local get() = OffsetDateTime(this, Klock.getLocalTimezoneOffsetMinutes(unix))

    @Deprecated("", ReplaceWith("utc"))
    fun toUtc(): DateTime = utc

    @Deprecated("", ReplaceWith("local"))
    fun toLocal() = local

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

    operator fun plus(delta: TimeSpan): DateTime = addMilliseconds(delta.milliseconds)
    operator fun minus(delta: TimeDistance): DateTime = this + -delta
    operator fun minus(delta: TimeSpan): DateTime = addMilliseconds(-delta.milliseconds)
    fun toString(format: String): String = toString(SimplerDateFormat(format))
    fun toString(format: SimplerDateFormat): String = format.format(this)

    //override fun hashCode(): Int
    //override fun equals(other: Any?): Boolean

    companion object {
        val EPOCH = DateTime(1970, 1, 1, 0, 0, 0) as UtcDateTime
        private val EPOCH_INTERNAL_MILLIS = EPOCH.internalMillis

        // Can produce errors on invalid dates
        operator fun invoke(
            year: Int,
            month: Int,
            day: Int,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            milliseconds: Int = 0
        ): DateTime {
            return UtcDateTime(
                UtcDateTime.dateToMillis(year, month, day) + UtcDateTime.timeToMillis(
                    hour,
                    minute,
                    second
                ) + milliseconds
            )
        }

        operator fun invoke(time: Long) = fromUnix(time)

        fun fromString(str: String) = SimplerDateFormat.parse(str)
        fun parse(str: String) = SimplerDateFormat.parse(str)

        fun fromUnix(time: Long): UtcDateTime = UtcDateTime(EPOCH_INTERNAL_MILLIS + time)
        fun fromUnixLocal(time: Long): OffsetDateTime = UtcDateTime(EPOCH_INTERNAL_MILLIS + time).toLocal()

        fun nowUnix() = Klock.currentTimeMillis()
        fun now() = fromUnix(nowUnix())
        fun nowLocal() = fromUnix(nowUnix()).toLocal()

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
            return UtcDateTime(
                UtcDateTime.dateToMillisUnchecked(year, month, day) + UtcDateTime.timeToMillisUnchecked(
                    hour,
                    minute,
                    second
                ) + milliseconds
            )
        }

        fun isLeapYear(year: Int): Boolean = Year.isLeap(year)
        fun daysInMonth(month: Int, isLeap: Boolean): Int = Month.days(month, isLeap)
        fun daysInMonth(month: Int, year: Int): Int = daysInMonth(month, isLeapYear(year))
    }
}
