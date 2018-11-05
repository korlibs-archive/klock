package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Class for measuring relative times with as much precission as possible.
 */
object PerformanceCounter {
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
}
