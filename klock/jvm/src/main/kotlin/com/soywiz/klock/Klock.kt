package com.soywiz.klock

import java.util.*

actual object Klock {
	actual fun currentTimeMillis(): Long = System.currentTimeMillis()
	actual fun currentTimeMillisDouble(): Double = System.currentTimeMillis().toDouble()
	actual fun getLocalTimezoneOffset(unix: Long): Int = TimeZone.getDefault().getOffset(unix) / 1000 / 60
}