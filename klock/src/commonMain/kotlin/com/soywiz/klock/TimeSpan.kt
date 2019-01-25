package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

/** [TimeSpan] representing this number as [nanoseconds] or 1 / 1_000_000_000 [seconds]. */
inline val Number.nanoseconds get() = TimeSpan.fromNanoseconds(this.toDouble())
/** [TimeSpan] representing this number as [microseconds] or 1 / 1_000_000 [seconds]. */
inline val Number.microseconds get() = TimeSpan.fromMicroseconds(this.toDouble())
/** [TimeSpan] representing this number as [milliseconds] or 1 / 1_000 [seconds]. */
inline val Number.milliseconds get() = TimeSpan.fromMilliseconds(this.toDouble())
/** [TimeSpan] representing this number as [seconds]. */
inline val Number.seconds get() = TimeSpan.fromSeconds((this.toDouble()))
/** [TimeSpan] representing this number as [minutes] or 60 [seconds]. */
inline val Number.minutes get() = TimeSpan.fromMinutes(this.toDouble())
/** [TimeSpan] representing this number as [hours] or 3_600 [seconds]. */
inline val Number.hours get() = TimeSpan.fromHours(this.toDouble())
/** [TimeSpan] representing this number as [days] or 86_400 [seconds]. */
inline val Number.days get() = TimeSpan.fromDays(this.toDouble())
/** [TimeSpan] representing this number as [weeks] or 604_800 [seconds]. */
inline val Number.weeks get() = TimeSpan.fromWeeks(this.toDouble())

/**
 * Represents a span of time, with [milliseconds] precision.
 *
 * It is an inline class wrapping [Double] instead of [Long] to work on JavaScript without allocations.
 */
@Suppress("DataClassPrivateConstructor")
inline class TimeSpan(
    /** Returns the total number of [seconds] for this [TimeSpan] (1 / 1_000 [seconds]) */
    val milliseconds: Double
) : Comparable<TimeSpan> {
    /** Returns the total number of [nanoseconds] for this [TimeSpan] (1 / 1_000_000_000 [seconds]) */
    val nanoseconds: Double get() = this.milliseconds / MILLIS_PER_NANOSECOND
    /** Returns the total number of [microseconds] for this [TimeSpan] (1 / 1_000_000 [seconds]) */
    val microseconds: Double get() = this.milliseconds / MILLIS_PER_MICROSECOND
    /** Returns the total number of [seconds] for this [TimeSpan] */
    val seconds: Double get() = this.milliseconds / MILLIS_PER_SECOND
    /** Returns the total number of [minutes] for this [TimeSpan] (60 [seconds]) */
    val minutes: Double get() = this.milliseconds / MILLIS_PER_MINUTE
    /** Returns the total number of [hours] for this [TimeSpan] (3_600 [seconds]) */
    val hours: Double get() = this.milliseconds / MILLIS_PER_HOUR
    /** Returns the total number of [days] for this [TimeSpan] (86_400 [seconds]) */
    val days: Double get() = this.milliseconds / MILLIS_PER_DAY
    /** Returns the total number of [weeks] for this [TimeSpan] (604_800 [seconds]) */
    val weeks: Double get() = this.milliseconds / MILLIS_PER_WEEK

    /** Returns the total number of [milliseconds] as a [Long] */
    val millisecondsLong: Long get() = milliseconds.toLong()
    /** Returns the total number of [milliseconds] as an [Int] */
    val millisecondsInt: Int get() = milliseconds.toInt()

    override fun compareTo(other: TimeSpan): Int = this.milliseconds.compareTo(other.milliseconds)

    operator fun unaryMinus() = TimeSpan(-this.milliseconds)
    operator fun unaryPlus() = TimeSpan(+this.milliseconds)

    operator fun plus(other: TimeSpan): TimeSpan = TimeSpan(this.milliseconds + other.milliseconds)
    operator fun plus(other: MonthSpan): DateTimeSpan = DateTimeSpan(other, this)
    operator fun plus(other: DateTimeSpan): DateTimeSpan = DateTimeSpan(other.monthSpan, other.timeSpan + this)

    operator fun minus(other: TimeSpan): TimeSpan = this + (-other)
    operator fun minus(other: MonthSpan): DateTimeSpan = this + (-other)
    operator fun minus(other: DateTimeSpan): DateTimeSpan = this + (-other)

    operator fun times(scale: Int): TimeSpan = TimeSpan(this.milliseconds * scale)
    operator fun times(scale: Double): TimeSpan = TimeSpan((this.milliseconds * scale))

    operator fun div(other: TimeSpan): Double = this.milliseconds / other.milliseconds
    operator fun rem(other: TimeSpan): TimeSpan = (this.milliseconds % other.milliseconds).milliseconds

    companion object {
        private const val MILLIS_PER_MICROSECOND = 1.0 / 1000.0
        private const val MILLIS_PER_NANOSECOND = MILLIS_PER_MICROSECOND / 1000.0

        /**
         * Zero time.
         */
        val ZERO = TimeSpan(0.0)

        /**
         * Represents an invalid TimeSpan.
         * Useful to represent an alternative "null" time lapse
         * avoiding the boxing of a nullable type.
         */
        val NULL = TimeSpan(Double.NaN)

        @PublishedApi
        internal fun fromMilliseconds(ms: Double) = when (ms) {
            0.0 -> ZERO
            else -> TimeSpan(ms)
        }

        @PublishedApi internal fun fromNanoseconds(s: Double) = fromMilliseconds(s * MILLIS_PER_NANOSECOND)
        @PublishedApi internal fun fromMicroseconds(s: Double) = fromMilliseconds(s * MILLIS_PER_MICROSECOND)
        @PublishedApi internal fun fromSeconds(s: Double) = fromMilliseconds(s * MILLIS_PER_SECOND)
        @PublishedApi internal fun fromMinutes(s: Double) = fromMilliseconds(s * MILLIS_PER_MINUTE)
        @PublishedApi internal fun fromHours(s: Double) = fromMilliseconds(s * MILLIS_PER_HOUR)
        @PublishedApi internal fun fromDays(s: Double) = fromMilliseconds(s * MILLIS_PER_DAY)
        @PublishedApi internal fun fromWeeks(s: Double) = fromMilliseconds(s * MILLIS_PER_WEEK)

        private val timeSteps = listOf(60, 60, 24)
        private fun toTimeStringRaw(totalMilliseconds: Double, components: Int = 3): String {
            var timeUnit = floor(totalMilliseconds / 1000.0).toInt()

            val out = arrayListOf<String>()

            for (n in 0 until components) {
                if (n == components - 1) {
                    out += timeUnit.padded(2)
                    break
                }
                val step = timeSteps.getOrNull(n) ?: throw RuntimeException("Just supported ${timeSteps.size} steps")
                val cunit = timeUnit % step
                timeUnit /= step
                out += cunit.padded(2)
            }

            return out.reversed().joinToString(":")
        }

        @PublishedApi
        internal fun toTimeString(totalMilliseconds: Double, components: Int = 3, addMilliseconds: Boolean = false): String {
            val milliseconds = (totalMilliseconds % 1000).toInt()
            val out = toTimeStringRaw(totalMilliseconds, components)
            return if (addMilliseconds) "$out.$milliseconds" else out
        }
    }

    override fun toString(): String = "${milliseconds.niceStr}ms"
}

/**
 * Formats this [TimeSpan] into something like `12:30:40.100`.
 *
 * For 3 hour, 20 minutes and 15 seconds
 *
 * 1 [components] (seconds): 12015
 * 2 [components] (minutes): 200:15
 * 3 [components] (hours)  : 03:20:15
 * 4 [components] (days)   : 00:03:20:15
 *
 * With milliseconds would add decimals to the seconds part.
 */
fun TimeSpan.toTimeString(components: Int = 3, addMilliseconds: Boolean = false): String =
    TimeSpan.toTimeString(milliseconds, components, addMilliseconds)

fun max(a: TimeSpan, b: TimeSpan): TimeSpan = max(a.milliseconds, b.milliseconds).milliseconds
fun min(a: TimeSpan, b: TimeSpan): TimeSpan = min(a.milliseconds, b.milliseconds).milliseconds
fun TimeSpan.clamp(min: TimeSpan, max: TimeSpan): TimeSpan = when {
    this < min -> min
    this > max -> max
    else -> this
}
