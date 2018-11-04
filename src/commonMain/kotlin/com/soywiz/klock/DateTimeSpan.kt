package com.soywiz.klock

import com.soywiz.klock.internal.*

data class DateTimeSpan(
    val monthSpan: MonthSpan,
    val timeSpan: TimeSpan
) : Comparable<DateTimeSpan> {
    constructor(
        years: Int = 0,
        months: Int = 0,
        weeks: Int = 0,
        days: Int = 0,
        hours: Int = 0,
        minutes: Int = 0,
        seconds: Int = 0,
        milliseconds: Double = 0.0
    ) : this(
        years.years + months.months,
        weeks.weeks + days.days + hours.hours + minutes.minutes + seconds.seconds + milliseconds.milliseconds
    )

    operator fun unaryMinus() = DateTimeSpan(-monthSpan, -timeSpan)
    operator fun unaryPlus() = DateTimeSpan(+monthSpan, +timeSpan)

    operator fun plus(other: TimeSpan) = DateTimeSpan(monthSpan, timeSpan + other)
    operator fun plus(other: MonthSpan) = DateTimeSpan(monthSpan + other, timeSpan)
    operator fun plus(other: DateTimeSpan) = DateTimeSpan(monthSpan + other.monthSpan, timeSpan + other.timeSpan)

    operator fun minus(other: TimeSpan) = this + -other
    operator fun minus(other: MonthSpan) = this + -other
    operator fun minus(other: DateTimeSpan) = this + -other

    inline operator fun times(times: Number) = times(times.toDouble())
    inline operator fun div(times: Number) = times(1.0 / times.toDouble())

    operator fun times(times: Double) = DateTimeSpan((monthSpan * times), (timeSpan * times))

    private class ComputedTime(val weeks: Int, val days: Int, val hours: Int, val minutes: Int, val seconds: Int, val milliseconds: Double) {
        companion object {
            operator fun invoke(time: TimeSpan): ComputedTime = Moduler(time.milliseconds).run {
                val weeks = int(MILLIS_PER_WEEK)
                val days = int(MILLIS_PER_DAY)
                val hours = int(MILLIS_PER_HOUR)
                val minutes = int(MILLIS_PER_MINUTE)
                val seconds = int(MILLIS_PER_SECOND)
                val milliseconds = double(1)
                return ComputedTime(weeks, days, hours, minutes, seconds, milliseconds)
            }
        }
    }

    private val computed by lazy { ComputedTime(timeSpan) }

    val years: Int get() = monthSpan.years
    val months: Int get() = monthSpan.months

    val totalMonths: Int get() = monthSpan.totalMonths
    val totalMilliseconds: Double get() = timeSpan.milliseconds

    val weeks: Int get() = computed.weeks
    val daysIncludingWeeks: Int get() = computed.days + (computed.weeks * 7)
    val days: Int get() = computed.days
    val hours: Int get() = computed.hours
    val minutes: Int get() = computed.minutes
    val seconds: Int get() = computed.seconds
    val milliseconds: Double get() = computed.milliseconds

    val secondsIncludingMilliseconds: Double get() = computed.seconds + computed.milliseconds / 1000.0

    // @TODO: If milliseconds overflow months this could not be exactly true. But probably will work in most cases.
    override fun compareTo(other: DateTimeSpan): Int {
        if (this.totalMonths != other.totalMonths) return this.monthSpan.compareTo(other.monthSpan)
        return this.timeSpan.compareTo(other.timeSpan)
    }

    fun toString(includeWeeks: Boolean): String = arrayListOf<String>().apply {
        if (years != 0) add("${years}Y")
        if (months != 0) add("${months}M")
        if (includeWeeks && weeks != 0) add("${weeks}W")
        if (days != 0 || (!includeWeeks && weeks != 0)) add("${if (includeWeeks) days else daysIncludingWeeks}D")
        if (hours != 0) add("${hours}H")
        if (minutes != 0) add("${minutes}m")
        if (seconds != 0 || milliseconds != 0.0) add("${secondsIncludingMilliseconds}s")
        if (monthSpan == 0.years && ((timeSpan == 0.seconds) || (timeSpan == (-0).seconds))) add("0s")
    }.joinToString(" ")

    override fun toString(): String = toString(includeWeeks = true)
}
