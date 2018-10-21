package com.soywiz.klock.internal

import com.soywiz.klock.*
import java.util.*

internal actual object KlockInternal {
    actual val currentTime: DateTime get() = DateTime(System.currentTimeMillis())
    actual val microClock: Double get() = (System.nanoTime() / 1000L).toDouble()
    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = TimeZone.getDefault().getOffset(time.unixLong).milliseconds
}