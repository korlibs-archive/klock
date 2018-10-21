package com.soywiz.klock

inline fun measureTime(callback: () -> Unit): TimeSpan {
    val start = Klock.currentTime
    callback()
    val end = Klock.currentTime
    return end - start
}

data class TimedResult<T>(val result: T, val time: TimeSpan)

inline fun <T : Any> measureTimeWithResult(callback: () -> T): TimedResult<T> {
    lateinit var result: T
    val elapsed = measureTime {
        result = callback()
    }
    return TimedResult(result, elapsed)
}

@Deprecated("", ReplaceWith("measureTime(callback).millisecondsInt"))
inline fun measureTimeMs(callback: () -> Unit): Int = measureTime(callback).millisecondsInt

@Deprecated("", ReplaceWith("measureTimeWithResult(callback)"))
inline fun <T : Any> measureTime(callback: () -> T): TimedResult<T> = measureTimeWithResult(callback)