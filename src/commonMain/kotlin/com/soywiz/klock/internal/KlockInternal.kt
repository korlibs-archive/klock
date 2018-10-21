package com.soywiz.klock.internal

import com.soywiz.klock.*

internal expect object KlockInternal {
    val currentTime: UtcDateTime
    val microClock: Double
    fun localTimezoneOffsetMinutes(time: UtcDateTime): TimeSpan
}