package com.soywiz.klock.internal

import com.soywiz.klock.*
import java.util.*

internal actual object KlockInternal {
    actual val currentTime: UtcDateTime get() = UtcDateTime(System.currentTimeMillis())
    actual val microClock: Double get() = (System.nanoTime() / 1000L).toDouble()
    actual fun localTimezoneOffsetMinutes(time: UtcDateTime): TimeSpan = TimeZone.getDefault().getOffset(time.unixLong).milliseconds
}