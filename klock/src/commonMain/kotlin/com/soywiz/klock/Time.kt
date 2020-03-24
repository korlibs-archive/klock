package com.soywiz.klock

import kotlin.math.abs

/**
 * Represents a union of [millisecond], [second], [minute] and [hour].
 */
inline class Time(val encoded: TimeSpan) : Comparable<Time> {
	companion object {
        /** Constructs a new [Time] from the [hour], [minute], [second] and [millisecond] components. */
		operator fun invoke(hour: Int, minute: Int = 0, second: Int = 0, millisecond: Int = 0): Time =
			Time(hour.hours + minute.minutes + second.seconds + millisecond.milliseconds)

		private const val DIV_MILLISECONDS = 1
		private const val DIV_SECONDS = DIV_MILLISECONDS * 1000
		private const val DIV_MINUTES = DIV_SECONDS * 60
		private const val DIV_HOURS = DIV_MINUTES * 60
	}
    /** The [millisecond] part. */
	val millisecond: Int get() = abs((encoded.millisecondsInt / DIV_MILLISECONDS) % 1000)
    /** The [second] part. */
    val second: Int get() = abs((encoded.millisecondsInt / DIV_SECONDS) % 60)
    /** The [minute] part. */
	val minute: Int get() = abs((encoded.millisecondsInt / DIV_MINUTES) % 60)
    /** The [hour] part. */
	val hour: Int get() = (encoded.millisecondsInt / DIV_HOURS)

    /** Converts this time to String formatting it like "00:00:00.000", "23:59:59.999" or "-23:59:59.999" if the [hour] is negative */
	override fun toString(): String = "${if (hour < 0) "-" else ""}${abs(hour).toString().padStart(2, '0')}:${abs(minute).toString().padStart(2, '0')}:${abs(second).toString().padStart(2, '0')}.${abs(millisecond).toString().padStart(3, '0')}"

	override fun compareTo(other: Time): Int = encoded.compareTo(other.encoded)
}

// @TODO: Do overflowing here instead of relying on DateTime. In fact, refact DateTime to handle the overflowing using this class
operator fun Time.plus(span: TimeSpan) = (DateTime.EPOCH + this.encoded + span).time
