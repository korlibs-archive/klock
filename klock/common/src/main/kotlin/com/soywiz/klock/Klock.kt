package com.soywiz.klock

expect object Klock {
	fun currentTimeMillis(): Long
	fun currentTimeMillisDouble(): Double
	fun getLocalTimezoneOffset(unix: Long): Int
}

