package com.soywiz.klock

/**
 * Exposed per-platform klock utilities.
 */
expect object Klock {
    /**
     * Returns the total milliseconds since unix epoch.
     */
    fun currentTimeMillis(): Long

    /**
     * Returns the total milliseconds since unix epoch.
     *
     * The same as `currentTimeMillis` but as double. To prevent allocation on
     * targets without Long support.
     */
    fun currentTimeMillisDouble(): Double

    /**
     * Returns a performance counter measure in microseconds.
     */
    fun microClock(): Double

    /**
     * Returns timezone offset in minutes, for a specified [unix] epoch in milliseconds.
     *
     * For example, GMT+01 would return 60.
     */
    fun getLocalTimezoneOffsetMinutes(unix: Long): Int
}

/**
 * Returns the current time as an [UtcDateTime].
 */
val Klock.currentTime: UtcDateTime get() = UtcDateTime(currentTimeMillis())

/**
 * Returns timezone offset as a [TimeSpan], for a specified [time].
 *
 * For example, GMT+01 would return 60.minutes.
 */
fun Klock.getLocalTimezoneOffset(time: UtcDateTime): TimeSpan = getLocalTimezoneOffsetMinutes(time.unix).minutes
