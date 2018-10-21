package com.soywiz.klock

import com.soywiz.klock.internal.*

object PerformanceCounter {
    /**
     * Returns a performance counter measure in microseconds.
     */
    val microseconds: Double get() = KlockInternal.microClock
}