package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Exposed per-platform klock utilities.
 */
object Klock

/**
 * Returns the current time as [DateTime].
 *
 * Note that since [DateTime] is inline, this property doesn't allocate on JavaScript.
 */
val Klock.currentTime: DateTime get() = DateTime(KlockInternal.currentTime)

/**
 * Returns a performance counter measure in microseconds.
 */
@Deprecated("", ReplaceWith("PerformanceCounter.microseconds"))
val Klock.microClock: Double get() = PerformanceCounter.microseconds

/**
 * Returns timezone offset as a [TimeSpan], for a specified [time].
 *
 * For example, GMT+01 would return 60.minutes.
 */
fun Klock.localTimezoneOffsetMinutes(time: DateTime): TimeSpan = KlockInternal.localTimezoneOffsetMinutes(time)

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
fun Klock.microClock(): Double = KlockInternal.microClock

@Deprecated("", ReplaceWith("Klock.currentTimeMillis", "com.soywiz.klock.Klock"))
fun Klock.currentTimeMillis(): Long = KlockInternal.currentTime.toLong()

@Deprecated("", ReplaceWith("Klock.currentTimeMillisDouble", "com.soywiz.klock.Klock"))
fun Klock.currentTimeMillisDouble(): Double = KlockInternal.currentTime

@Deprecated("", ReplaceWith("Klock.localTimezoneOffsetMinutes(DateTime(unix)).minutes.toInt()", "com.soywiz.klock.Klock"))
fun Klock.getLocalTimezoneOffsetMinutes(unix: Long): Int = localTimezoneOffsetMinutes(DateTime(unix)).minutes.toInt()

//@Deprecated("", ReplaceWith("PerformanceCounter.microseconds"))
//val Klock.microClock: Double get() = PerformanceCounter.microseconds
