package com.soywiz.klock

expect object Klock {
	fun currentTimeMillis(): Long
	fun getLocalTimezoneOffset(unix: Long): Int
}

