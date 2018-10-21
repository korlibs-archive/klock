package com.soywiz.klock

import kotlin.browser.*
import kotlin.math.*

actual object Klock {
    actual fun currentTimeMillis(): Long = js("Date.now()").unsafeCast<Double>().toLong()
    actual fun currentTimeMillisDouble(): Double = js("Date.now()").unsafeCast<Double>()

    actual fun getLocalTimezoneOffsetMinutes(unix: Long): Int {
        @Suppress("UNUSED_VARIABLE")
        val rtime = unix.toDouble()
        return js("-(new Date(rtime)).getTimezoneOffset()").unsafeCast<Int>()
    }

    actual fun microClock(): Double = floor(window.performance.now() * 1000)
}