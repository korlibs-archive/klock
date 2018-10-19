package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

inline val Number.microseconds get() = TimeSpan.fromMilliseconds(this.toDouble() / 1000.0)
inline val Number.milliseconds get() = TimeSpan.fromMilliseconds(this.toDouble())
inline val Number.seconds get() = TimeSpan.fromMilliseconds((this.toDouble() * 1000.0))

@Suppress("DataClassPrivateConstructor")
inline class TimeSpan(private val ms: Double) : Comparable<TimeSpan> {
    val microseconds: Double get() = this.ms * 1000.0
    val milliseconds: Double get() = this.ms
    val millisecondsLong: Long get() = this.ms.toLong()
    val millisecondsInt: Int get() = this.ms.toInt()
    val seconds: Double get() = this.ms / 1000.0

    companion object {
        /**
         * Zero time
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

        private val timeSteps = listOf(60, 60, 24)
        private fun toTimeStringRaw(totalMilliseconds: Double, components: Int = 3): String {
            var timeUnit = (totalMilliseconds / 1000.0).roundToInt()

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

        inline fun toTimeString(totalMilliseconds: Number, components: Int = 3, addMilliseconds: Boolean = false): String {
            return toTimeString(totalMilliseconds.toDouble(), components, addMilliseconds)
        }

        @PublishedApi
        internal fun toTimeString(totalMilliseconds: Double, components: Int = 3, addMilliseconds: Boolean = false): String {
            val milliseconds = totalMilliseconds % 1000L
            val out = toTimeStringRaw(totalMilliseconds, components)
            return if (addMilliseconds) "$out.$milliseconds" else out
        }
    }

    override fun compareTo(other: TimeSpan): Int = this.ms.compareTo(other.ms)

    operator fun plus(other: TimeSpan): TimeSpan = TimeSpan(this.ms + other.ms)
    operator fun minus(other: TimeSpan): TimeSpan = TimeSpan(this.ms - other.ms)
    operator fun times(scale: Int): TimeSpan = TimeSpan(this.ms * scale)
    operator fun times(scale: Double): TimeSpan = TimeSpan((this.ms * scale))
}

fun TimeSpan.toTimeString(components: Int = 3, addMilliseconds: Boolean = false): String =
    TimeSpan.toTimeString(milliseconds, components, addMilliseconds)

