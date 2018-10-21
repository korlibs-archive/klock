package com.soywiz.klock.internal

import com.soywiz.klock.*
import kotlinx.cinterop.*
import platform.posix.*

internal actual object KlockInternal {
    actual val currentTime: Double
        get() = memScoped {
            val timeVal = alloc<timeval>()
            mingw_gettimeofday(timeVal.ptr, null) // mingw: doesn't expose gettimeofday, but mingw_gettimeofday
            val sec = timeVal.tv_sec
            val usec = timeVal.tv_usec
            ((sec * 1_000L) + (usec / 1_000L)).toDouble()
        }

    actual val microClock: Double
        get() = memScoped {
            val timeVal = alloc<timeval>()
            mingw_gettimeofday(timeVal.ptr, null)
            val sec = timeVal.tv_sec
            val usec = timeVal.tv_usec
            ((sec * 1_000_000L) + usec).toDouble()
        }

    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = 0.milliseconds
}
