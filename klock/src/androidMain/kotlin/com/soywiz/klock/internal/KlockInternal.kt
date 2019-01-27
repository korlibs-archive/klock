package com.soywiz.klock.internal

import com.soywiz.klock.*
import java.util.*

internal actual object KlockInternal {
    actual val currentTime: Double get() = (System.currentTimeMillis()).toDouble()
    actual val microClock: Double get() = (System.nanoTime() / 1000L).toDouble()
    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = TimeZone.getDefault().getOffset(time.unixMillisLong).milliseconds
}