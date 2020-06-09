package com.soywiz.klock.internal

import com.soywiz.klock.*
import com.soywiz.klock.hr.HRTimeSpan

internal expect object KlockInternal {
    val currentTime: Double
    val hrNow: HRTimeSpan
    fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan
    fun sleep(time: HRTimeSpan)
}

internal fun spinlock(time: HRTimeSpan) {
    val start = HRTimeSpan.now()
    while (HRTimeSpan.now() - start < time) Unit
}

expect interface Serializable
