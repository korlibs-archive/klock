package com.soywiz.klock.internal

import com.soywiz.klock.*

internal expect object KlockInternal {
    val currentTime: DateTime
    val microClock: Double
    fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan
}