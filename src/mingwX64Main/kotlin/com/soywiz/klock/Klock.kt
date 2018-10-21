package com.soywiz.klock

import kotlinx.cinterop.*
import platform.posix.*

actual object Klock {
    actual val currentTime: UtcDateTime get() = memScoped {
        val timeVal = alloc<timeval>()
        mingw_gettimeofday(timeVal.ptr, null) // mingw: doesn't expose gettimeofday, but mingw_gettimeofday
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        UtcDateTime((sec * 1_000L) + (usec / 1_000L))
    }

    actual val microClock: Double get() = memScoped {
        val timeVal = alloc<timeval>()
        mingw_gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        ((sec * 1_000_000L) + usec).toDouble()
    }

    actual fun localTimezoneOffsetMinutes(time: UtcDateTime): TimeSpan = 0.milliseconds
}
