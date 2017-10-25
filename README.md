# klock
Consistent and portable date and time utilities for multiplatform kotlin

[![Build Status](https://travis-ci.org/soywiz/klock.svg?branch=master)](https://travis-ci.org/soywiz/klock)
[![Maven Version](https://img.shields.io/github/tag/soywiz/klock.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22klock%22)

Use with gradle (uploaded to maven central):

```
compile "com.soywiz:klock:0.1.0" // jvm/android
compile "com.soywiz:klock-js:0.1.0" // js
compile "com.soywiz:klock-common:0.1.0" // common (just expect 2 decls in Klock)
```

```kotlin
object Klock {
	fun currentTimeMillis(): Long
	fun getLocalTimezoneOffset(unix: Long): Int
}

enum class DayOfWeek {
	Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
}

enum class Month {
	January, February, March, April, May, June,
	July, August, September, October, November, December;

	val index0: Int

	fun days(isLeap: Boolean): Int
	fun daysToStart(isLeap: Boolean): Int
	fun daysToEnd(isLeap: Boolean): Int

	fun days(year: Int): Int
	fun daysToStart(year: Int): Int
	fun daysToEnd(year: Int): Int
}

object Year {
    fun isLeap(year: Int): Boolean
}

interface DateTime {
	companion object {
		val EPOCH: DateTime
		operator fun invoke(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime
		operator fun invoke(time: Long): DateTime

		fun fromUnix(time: Long): DateTime
		fun fromUnixLocal(time: Long): DateTime

		fun nowUnix(): Long
		fun now(): DateTime
		fun nowLocal(): DateTime
		fun createAdjusted(year: Int, month: Int, day: Int, hour: Int = 0, minute: Int = 0, second: Int = 0, milliseconds: Int = 0): DateTime
		fun isLeapYear(year: Int): Boolean
		fun daysInMonth(month: Int, isLeap: Boolean): Int
		fun daysInMonth(month: Int, year: Int): Int
	}
	
	val year: Int
	val month: Int
	val dayOfWeekInt: Int
	val dayOfMonth: Int
	val dayOfYear: Int
	val hours: Int
	val minutes: Int
	val seconds: Int
	val milliseconds: Int
	val timeZone: String
	val unix: Long
	val offset: Int
	val utc: UtcDateTime
	fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime

	val dayOfWeek: DayOfWeek
	val month0: Int
	val month1: Int
	val monthEnum: Month
	
	fun toUtc(): DateTime
	fun toLocal(): DateTime
	fun addOffset(offset: Int): DateTime
	fun toOffset(offset: Int): DateTime
	fun addYears(delta: Int): DateTime
	fun addMonths(delta: Int): DateTime
	fun addHours(delta: Double): DateTime
	fun addMinutes(delta: Double): DateTime
	fun addSeconds(delta: Double): DateTime
	fun addMilliseconds(delta: Double): DateTime
	fun addMilliseconds(delta: Long): DateTime

	operator fun plus(delta: TimeDistance): DateTime
	operator fun minus(delta: TimeDistance): DateTime

	fun toString(format: String): String = SimplerDateFormat(format).format(this)
}


data class TimeDistance(val years: Int = 0, val months: Int = 0, val days: Double = 0.0, val hours: Double = 0.0, val minutes: Double = 0.0, val seconds: Double = 0.0, val milliseconds: Double = 0.0) {
	operator fun unaryMinus(): TimeDistance
	operator fun minus(other: TimeDistance): TimeDistance
	operator fun plus(other: TimeDistance): TimeDistance
	operator fun times(times: Double): TimeDistance
}

data class TimeSpan {
	val milliseconds: Int
	val seconds: Double

	companion object {
		val ZERO = TimeSpan(0)
	}

	override fun compareTo(other: TimeSpan): Int
	operator fun plus(other: TimeSpan): TimeSpan
	operator fun minus(other: TimeSpan): TimeSpan
	operator fun times(scale: Int): TimeSpan
	operator fun times(scale: Double): TimeSpan
}

inline val Int.years: TimeDistance
inline val Int.months: TimeDistance
inline val Number.days: TimeDistance
inline val Number.hours: TimeDistance
inline val Number.minutes: TimeDistance
inline val Number.seconds: TimeSpan
inline val Number.milliseconds: TimeSpan

class SimplerDateFormat(val format: String) {
	companion object {
		val DEFAULT_FORMAT: SimplerDateFormat // "EEE, dd MMM yyyy HH:mm:ss z"
	}

	fun format(date: Long): String
	fun format(dd: DateTime): String
	fun parse(str: String): Long
	fun parseDate(str: String): DateTime
}
```
