package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Represents a time zone offset with millisecond precision. Usually minute is enough.
 * Can be used along [DateTimeWithOffset] to construct non universal, local times.
 *
 * This class is inlined so no boxing should be required.
 */
inline class TimezoneOffset(val milliseconds: Double) {
    val time get() = milliseconds.milliseconds

    companion object {
        /**
         * Constructs a new [TimezoneOffset] from a [TimeSpan].
         */
        operator fun invoke(time: TimeSpan) = TimezoneOffset(time.milliseconds)

        /**
         * Returns timezone offset as a [TimeSpan], for a specified [time].
         *
         * For example, GMT+01 would return 60.minutes.
         */
        fun local(time: DateTime): TimezoneOffset = KlockInternal.localTimezoneOffsetMinutes(time).offset
    }
}

/**
 * A [TimeSpan] as a [TimezoneOffset].
 */
val TimeSpan.offset get() = TimezoneOffset(this)
