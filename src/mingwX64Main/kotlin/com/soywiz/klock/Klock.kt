package com.soywiz.klock

import kotlinx.cinterop.*
import platform.posix.*

actual object Klock {
    actual fun currentTimeMillis(): Long = memScoped {
        val timeVal = alloc<timeval>()
        mingw_gettimeofday(timeVal.ptr, null) // mingw: doesn't expose gettimeofday, but mingw_gettimeofday
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        (sec * 1_000L) + (usec / 1_000L)
    }

    actual fun microClock(): Double = memScoped {
        val timeVal = alloc<timeval>()
        mingw_gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        ((sec * 1_000_000L) + usec).toDouble()
    }

    actual fun currentTimeMillisDouble(): Double = currentTimeMillis().toDouble()

    actual fun getLocalTimezoneOffset(unix: Long): Int = 0
}
