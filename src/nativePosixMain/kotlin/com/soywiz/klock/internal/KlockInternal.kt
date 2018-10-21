package com.soywiz.klock.internal

import com.soywiz.klock.*
import kotlinx.cinterop.*
import platform.posix.*

internal actual object KlockInternal {
    actual val currentTime: DateTime get() = memScoped {
        val timeVal = alloc<timeval>()
        gettimeofday(timeVal.ptr, null)
        val sec = timeVal.tv_sec
        val usec = timeVal.tv_usec
        DateTime((sec * 1_000L) + (usec / 1_000L))
    }

    actual val microClock: Double get() = memScoped {
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

    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = memScoped {
        val t = alloc<time_tVar>()
        val tm = alloc<tm>()
        t.value = (time.unixLong / 1000L).convert()
        localtime_r(t.ptr, tm.ptr)
        tm.tm_gmtoff.toInt().seconds
    }
}
