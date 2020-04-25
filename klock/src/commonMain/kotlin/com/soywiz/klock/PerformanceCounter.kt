package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Class for measuring relative times with as much precision as possible.
 */
inline class PerformanceCounter(val microseconds: Double) {
    val nanoseconds: Double get() = microseconds * 1000.0
    val milliseconds: Double get() = microseconds / 1000.0
    val timespan: TimeSpan get() = this.microseconds.microseconds

    companion object {
        /**
         * Returns a performance counter measure in nanoseconds.
         */
        val nanoseconds: Double get() = KlockInternal.microClock * 1000.0

        /**
         * Returns a performance counter measure in microseconds.
         */
        val microseconds: Double get() = KlockInternal.microClock

        /**
         * Returns a performance counter measure in milliseconds.
         */
        val milliseconds: Double get() = KlockInternal.microClock / 1000.0

        /**
         * Returns a performance counter as a [TimeSpan].
         */
        val reference: TimeSpan get() = KlockInternal.microClock.microseconds

        /**
         * Returns a performance counter as a [PerformanceCounter].
         */
        val counter: PerformanceCounter get() = PerformanceCounter(microseconds)
    }
}
