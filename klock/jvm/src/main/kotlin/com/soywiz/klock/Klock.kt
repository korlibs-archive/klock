package com.soywiz.klock

import java.util.*

actual object Klock {
	actual fun currentTimeMillis(): Long = System.currentTimeMillis()
	actual fun getLocalTimezoneOffset(unix: Long): Int = TimeZone.getDefault().getOffset(unix) / 1000 / 60
}