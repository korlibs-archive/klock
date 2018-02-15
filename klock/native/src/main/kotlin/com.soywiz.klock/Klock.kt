package com.soywiz.klock

import kotlinx.cinterop.*
import platform.posix.*

actual object Klock {
    actual val VERSION: String = KlockExt.VERSION

    actual fun currentTimeMillis(): Long = memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        (sec * 1000L) + (usec / 1_000_000L)
    }

    actual fun currentTimeMillisDouble(): Double {
        return currentTimeMillis().toDouble()
    }

    actual fun getLocalTimezoneOffset(unix: Long): Int = memScoped {
        val t = alloc<time_tVar>()
        val tm = alloc<tm>()
        t.value = unix / 1000L
        localtime_r(t.ptr, tm.ptr)
        tm.tm_gmtoff.toInt() / 60
    }
}
