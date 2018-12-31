package com.soywiz.klock.internal

import com.soywiz.klock.*
import kotlin.browser.*
import kotlin.math.*

private external val process: dynamic

private val isNode = jsTypeOf(window) == "undefined"
private val initialHrTime by lazy { process.hrtime() }

internal actual object KlockInternal {
    actual val currentTime: Double get() = (js("Date.now()").unsafeCast<Double>())

    actual val microClock: Double get() {
        //return if (isNode) {
            val result = process.hrtime(initialHrTime)
            return (result[0] * 1_000_000 + floor((result[1] / 1_000) as Double)) as Double
        //} else {
        //    floor(window.performance.now() * 1000)
        //}
    }

    actual fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan {
        @Suppress("UNUSED_VARIABLE")
        val rtime = time.unixMillisDouble
        return js("-(new Date(rtime)).getTimezoneOffset()").unsafeCast<Int>().minutes
    }
}