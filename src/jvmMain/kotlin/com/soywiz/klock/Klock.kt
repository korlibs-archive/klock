package com.soywiz.klock

import java.util.*

actual object Klock {
    actual val currentTime: UtcDateTime get() = UtcDateTime(System.currentTimeMillis())
    actual val microClock: Double get() = (System.nanoTime() / 1000L).toDouble()
    actual fun localTimezoneOffsetMinutes(time: UtcDateTime): TimeSpan = TimeZone.getDefault().getOffset(time.unixLong).milliseconds
}