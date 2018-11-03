package com.soywiz.klock

import com.soywiz.klock.internal.*

inline class TimezoneOffset(val milliseconds: Double) {
    val time get() = milliseconds.milliseconds

    companion object {
        operator fun invoke(time: TimeSpan) = TimezoneOffset(time.milliseconds)

        /**
         * Returns timezone offset as a [TimeSpan], for a specified [time].
         *
         * For example, GMT+01 would return 60.minutes.
         */
        fun local(time: DateTime): TimeSpan = KlockInternal.localTimezoneOffsetMinutes(time)
    }
}

/**
 * A [TimeSpan] as a [TimezoneOffset].
 */
val TimeSpan.offset get() = TimezoneOffset(this)
