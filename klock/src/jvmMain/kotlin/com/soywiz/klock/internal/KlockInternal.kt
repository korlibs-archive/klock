package com.soywiz.klock.internal

import com.soywiz.klock.*
import java.util.*

internal actual object KlockInternal {
    actual val currentTime: Double get() = CurrentKlockInternalJvm.currentTime
    actual val microClock: Double get() = CurrentKlockInternalJvm.microClock
    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = CurrentKlockInternalJvm.localTimezoneOffsetMinutes(time)
}

inline fun <T> TemporalKlockInternalJvm(impl: KlockInternalJvm, callback: () -> T): T {
    val old = CurrentKlockInternalJvm
    CurrentKlockInternalJvm = impl
    try {
        return callback()
    } finally {
        CurrentKlockInternalJvm = old
    }
}

var CurrentKlockInternalJvm = object : KlockInternalJvm {
}

interface KlockInternalJvm {
    val currentTime: Double get() = (System.currentTimeMillis()).toDouble()
    val microClock: Double get() = (System.nanoTime() / 1000L).toDouble()
    fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = TimeZone.getDefault().getOffset(time.unixMillisLong).milliseconds
}
