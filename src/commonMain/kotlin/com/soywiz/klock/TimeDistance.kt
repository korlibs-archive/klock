package com.soywiz.klock

inline val Int.years get() = TimeDistance(years = this)
inline val Int.months get() = TimeDistance(months = this)
inline val Number.days get() = TimeDistance(days = this.toDouble())
inline val Number.weeks get() = TimeDistance(days = this.toDouble() * 7)
inline val Number.hours get() = TimeDistance(hours = this.toDouble())
inline val Number.minutes get() = TimeDistance(minutes = this.toDouble())

data class TimeDistance(
	val years: Int = 0,
	val months: Int = 0,
	val days: Double = 0.0,
	val hours: Double = 0.0,
	val minutes: Double = 0.0,
	val seconds: Double = 0.0,
	val milliseconds: Double = 0.0
) : Comparable<TimeDistance> {
	operator fun unaryMinus() = TimeDistance(-years, -months, -days, -hours, -minutes, -seconds, -milliseconds)

	operator fun minus(other: TimeDistance) = this + -other

	operator fun plus(other: TimeDistance) = TimeDistance(
		years + other.years,
		months + other.months,
		days + other.days,
		hours + other.hours,
		minutes + other.minutes,
		seconds + other.seconds,
		milliseconds + other.milliseconds
	)

	@Suppress("NOTHING_TO_INLINE")
	inline operator fun times(times: Number) = times(times.toDouble())

	operator fun times(times: Double) = TimeDistance(
		(years * times).toInt(),
		(months * times).toInt(),
		days * times,
		hours * times,
		minutes * times,
		seconds * times,
		milliseconds * times
	)

	val totalMonths get() = years * 12 + months
	val totalMilliseconds: Long by lazy { (days * MILLIS_PER_DAY + hours * MILLIS_PER_HOUR + minutes * MILLIS_PER_MINUTE + seconds * MILLIS_PER_SECOND + milliseconds).toLong() }

	// @TODO: If milliseconds overflow months this could not be exactly true. But probably will work in most cases.
	override fun compareTo(other: TimeDistance): Int {
		if (this.totalMonths != other.totalMonths) return this.totalMonths.compareTo(other.totalMonths)
		return this.totalMilliseconds.compareTo(other.totalMilliseconds)
	}
}
