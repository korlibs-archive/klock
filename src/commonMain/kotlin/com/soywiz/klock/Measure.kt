package com.soywiz.klock

/**
 * Executes a [callback] and measure the time it takes to complete.
 */
inline fun measureTime(callback: () -> Unit): TimeSpan {
    val start = Klock.currentTime
    callback()
    val end = Klock.currentTime
    return end - start
}

/**
 * Executes a [callback] measuring the time it takes to complete,
 * returning a [TimedResult] with the time and the returning value of the callback.
 */
inline fun <T : Any> measureTimeWithResult(callback: () -> T): TimedResult<T> {
    lateinit var result: T
    val elapsed = measureTime {
        result = callback()
    }
    return TimedResult(result, elapsed)
}

/**
 * Represents a [result] associated to a [time].
 */
data class TimedResult<T>(val result: T, val time: TimeSpan)

@Deprecated("", ReplaceWith("measureTime(callback).millisecondsInt"))
inline fun measureTimeMs(callback: () -> Unit): Int = measureTime(callback).millisecondsInt

@Deprecated("", ReplaceWith("measureTimeWithResult(callback)"))
inline fun <T : Any> measureTime(callback: () -> T): TimedResult<T> = measureTimeWithResult(callback)