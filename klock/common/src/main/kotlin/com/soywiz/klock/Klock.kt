package com.soywiz.klock

expect object Klock {
	val VERSION: String
	fun currentTimeMillis(): Long
	fun currentTimeMillisDouble(): Double
	fun getLocalTimezoneOffset(unix: Long): Int
}

