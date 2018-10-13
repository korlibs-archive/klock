package com.soywiz.klock

inline fun measureTimeMs(callback: () -> Unit): Int {
	val start = Klock.currentTimeMillisDouble()
	callback()
	val end = Klock.currentTimeMillisDouble()
	return (end - start).toInt()
}

data class TimedResult<T>(val result: T, val time: TimeSpan)

inline fun <T : Any> measureTime(callback: () -> T): TimedResult<T> {
	lateinit var result: T
	val ms = measureTimeMs {
		result = callback()
	}
	return TimedResult(result, ms.milliseconds)
}