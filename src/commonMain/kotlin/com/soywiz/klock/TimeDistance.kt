package com.soywiz.klock

import com.soywiz.klock.internal.*

inline val Int.years get() = TimeDistance(12 * this, 0.milliseconds)
inline val Int.months get() = TimeDistance(this, 0.milliseconds)

data class TimeDistance(
    val totalMonths: Int,
    val totalTime: TimeSpan
) : Comparable<TimeDistance> {
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
        years * 12 + months,
        (weeks * MILLIS_PER_WEEK + days * MILLIS_PER_DAY + hours * MILLIS_PER_HOUR + minutes * MILLIS_PER_MINUTE + seconds * MILLIS_PER_SECOND + milliseconds).milliseconds
    )

    operator fun unaryMinus() = TimeDistance(-totalMonths, -totalTime)

    operator fun minus(other: TimeDistance) = this + -other
    operator fun minus(other: TimeSpan) = this + -other

    operator fun plus(other: TimeDistance) = TimeDistance(totalMonths + other.totalMonths, totalTime + other.totalTime)
    operator fun plus(other: TimeSpan) = TimeDistance(totalMonths, totalTime + other)

    inline operator fun times(times: Number) = times(times.toDouble())
    inline operator fun div(times: Number) = times(1.0 / times.toDouble())

    operator fun times(times: Double) = TimeDistance((totalMonths * times).toInt(), (totalTime * times))

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

    private val computed by lazy { ComputedTime(totalTime) }

    val years: Int get() = totalMonths / 12
    val months: Int get() = totalMonths % 12

    val totalMilliseconds: Long get() = totalTime.millisecondsLong

    val weeks: Int get() = computed.weeks
    val days: Int get() = computed.days
    val hours: Int get() = computed.hours
    val minutes: Int get() = computed.minutes
    val seconds: Int get() = computed.seconds
    val milliseconds: Double get() = computed.milliseconds

    // @TODO: If milliseconds overflow months this could not be exactly true. But probably will work in most cases.
    override fun compareTo(other: TimeDistance): Int {
        if (this.totalMonths != other.totalMonths) return this.totalMonths.compareTo(other.totalMonths)
        return this.totalMilliseconds.compareTo(other.totalMilliseconds)
    }

    override fun toString(): String {
        return arrayListOf<String>().apply {
            if (years != 0) add("${years}Y")
            if (months != 0) add("${months}M")
            if (weeks != 0) add("${weeks}W")
            if (days != 0) add("${days}D")
            if (hours != 0) add("${hours}H")
            if (minutes != 0) add("${minutes}m")
            if (seconds != 0 || milliseconds != 0.0) add("${seconds + milliseconds / 1000.0}s")
        }.joinToString(" ")
    }
}

/*
data class TimeDistance(
    val years: Int = 0,
    val months: Int = 0,
    val days: Double = 0.0,
    val hours: Double = 0.0,
    val minutes: Double = 0.0,
    val seconds: Double = 0.0,
    val milliseconds: Double = 0.0
) : Comparable<TimeDistance> {
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
*/
