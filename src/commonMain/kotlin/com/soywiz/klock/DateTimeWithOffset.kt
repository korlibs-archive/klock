package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

data class DateTimeWithOffset(
    val base: DateTime,
    val offset: TimezoneOffset
) : Comparable<DateTimeWithOffset> {
    companion object {
        fun adjusted(adjusted: DateTime, offset: TimezoneOffset) = DateTimeWithOffset(adjusted + offset.time, offset)
        fun fromUnixLocal(time: Long): DateTimeWithOffset = fromUnixLocal(time.toDouble())
        fun fromUnixLocal(time: Double): DateTimeWithOffset = DateTime(time).localBase
        fun nowLocal(): DateTimeWithOffset = DateTime.fromUnix(DateTime.nowUnix()).localBase
    }

    //val adjusted: DateTime by lazy { (base + offset.minutes) } // @TODO: Kotlin inline bug! (ClassCastException)
    val adjusted: DateTime get() = (base - offset.time)

    val local: DateTime get() = base
    val utc: DateTime get() = adjusted

    private val deltaTotalMinutesAbs: Int = abs(offset.time.minutes.toInt())
    val positive: Boolean get() = offset.time >= 0.minutes
    val deltaHoursAbs: Int get() = deltaTotalMinutesAbs / 60
    val deltaMinutesAbs: Int get() = deltaTotalMinutesAbs % 60

    val offsetMinutes get() = offset.time.minutes.toInt()

    val year: Year get() = base.year
    val yearInt: Int get() = base.yearInt

    val month: Month get() = base.month
    val month0: Int get() = base.month0
    val month1: Int get() = base.month1

    val dayOfMonth: Int get() = base.dayOfMonth

    val dayOfWeek: DayOfWeek get() = base.dayOfWeek
    val dayOfWeekInt: Int get() = base.dayOfWeekInt

    val dayOfYear: Int get() = base.dayOfYear

    val hours: Int get() = base.hours
    val minutes: Int get() = base.minutes
    val seconds: Int get() = base.seconds
    val milliseconds: Int get() = base.milliseconds

    val timeZone: String by lazy {
        val sign = if (positive) "+" else "-"
        val hour = deltaHoursAbs.padded(2)
        val minute = deltaMinutesAbs.padded(2)
        if (offset.time == 0.minutes) "UTC" else "GMT$sign$hour$minute"
    }

    fun toOffset(offset: TimeSpan) = DateTimeWithOffset(this.base, offset.offset)
    fun toOffset(minutes: Int) = toOffset(minutes.minutes)

    fun addOffset(offset: TimeSpan) = DateTimeWithOffset(this.base, (this.offset.time + offset).offset)
    fun addOffset(minutes: Int) = addOffset(minutes.minutes)

    fun add(dateSpan: MonthSpan, timeSpan: TimeSpan): DateTimeWithOffset = DateTimeWithOffset(base.add(dateSpan, timeSpan), offset)

    operator fun plus(delta: MonthSpan) = add(delta, 0.milliseconds)
    operator fun plus(delta: DateTimeSpan) = add(delta.monthSpan, delta.timeSpan)
    operator fun plus(delta: TimeSpan) = add(0.months, delta)

    operator fun minus(delta: MonthSpan) = this + (-delta)
    operator fun minus(delta: DateTimeSpan) = this + (-delta)
    operator fun minus(delta: TimeSpan) = this + (-delta)

    override fun hashCode(): Int = adjusted.hashCode()
    override fun equals(other: Any?): Boolean = other is DateTimeWithOffset && this.adjusted.unixMillisDouble == other.adjusted.unixMillisDouble
    override fun compareTo(other: DateTimeWithOffset): Int = this.utc.unixMillis.compareTo(other.utc.unixMillis)

    fun toString(format: DateFormat): String = format.format(this)
    fun toString(format: String): String = DateFormat(format).format(this)
    override fun toString(): String = DateFormat.DEFAULT_FORMAT.format(this)
}
