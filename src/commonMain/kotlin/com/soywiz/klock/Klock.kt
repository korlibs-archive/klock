package com.soywiz.klock

/**
 * Exposed per-platform klock utilities.
 */
expect object Klock {
    /**
     * Returns the current time as [UtcDateTime].
     *
     * Note that since [UtcDateTime] is inline, this property doesn't allocate on JavaScript.
     */
    val currentTime: UtcDateTime

    /**
     * Returns a performance counter measure in microseconds.
     */
    val microClock: Double

    /**
     * Returns timezone offset as a [TimeSpan], for a specified [time].
     *
     * For example, GMT+01 would return 60.minutes.
     */
    fun localTimezoneOffsetMinutes(time: UtcDateTime): TimeSpan
}

/**
 * Returns the total milliseconds since unix epoch.
 *
 * The same as [currentTimeMillisLong] but as double. To prevent allocation on
 * targets without Long support.
 */
val Klock.currentTimeMillisDouble get() = currentTime.unixDouble

/**
 * Returns the total milliseconds since unix epoch.
 */
val Klock.currentTimeMillisLong get() = currentTime.unixLong

@Deprecated("", ReplaceWith("Klock.microClock", "com.soywiz.klock.Klock"))
fun Klock.microClock(): Double = microClock

@Deprecated("", ReplaceWith("Klock.currentTimeMillis", "com.soywiz.klock.Klock"))
fun Klock.currentTimeMillis(): Long = currentTimeMillisLong

@Deprecated("", ReplaceWith("Klock.currentTimeMillisDouble", "com.soywiz.klock.Klock"))
fun Klock.currentTimeMillisDouble(): Double = currentTimeMillisDouble

@Deprecated("", ReplaceWith("Klock.localTimezoneOffsetMinutes(UtcDateTime(unix)).minutes.toInt()", "com.soywiz.klock.Klock"))
fun Klock.getLocalTimezoneOffsetMinutes(unix: Long): Int = localTimezoneOffsetMinutes(UtcDateTime(unix)).minutes.toInt()
