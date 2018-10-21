package com.soywiz.klock.internal

import com.soywiz.klock.*
import kotlin.browser.*
import kotlin.math.*

internal actual object KlockInternal {
    actual val currentTime: Double get() = (js("Date.now()").unsafeCast<Double>())

    actual val microClock: Double get() = floor(window.performance.now() * 1000)

    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan {
        @Suppress("UNUSED_VARIABLE")
        val rtime = time.unixDouble
        return js("-(new Date(rtime)).getTimezoneOffset()").unsafeCast<Int>().minutes
    }
}