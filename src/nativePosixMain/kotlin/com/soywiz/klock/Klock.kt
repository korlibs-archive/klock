package com.soywiz.klock

import kotlinx.cinterop.*
import platform.posix.*

actual object Klock {
    actual fun currentTimeMillis(): Long = memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        (sec * 1_000L) + (usec / 1_000L)
    }

    actual fun microClock(): Double = memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        ((sec * 1_000_000L) + usec).toDouble()
    }

    // @TODO: kotlin-native bug: https://github.com/JetBrains/kotlin-native/pull/1901
    //private val microStart = kotlin.system.getTimeMicros()
    //actual fun currentTimeMillis(): Long = kotlin.system.getTimeMillis()
    //actual fun microClock(): Double = (kotlin.system.getTimeMicros() - microStart).toDouble()

    actual fun currentTimeMillisDouble(): Double = currentTimeMillis().toDouble()

    actual fun getLocalTimezoneOffsetMinutes(unix: Long): Int = memScoped {
        val t = alloc<time_tVar>()
        val tm = alloc<tm>()
        t.value = (unix / 1000L).convert()
        localtime_r(t.ptr, tm.ptr)
        tm.tm_gmtoff.toInt() / 60
    }
}
