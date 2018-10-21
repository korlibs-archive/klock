package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

data class DateTimeWithOffset(
    val base: DateTime,
    val offset: Int
) : Comparable<DateTimeWithOffset> {
    val adjusted: DateTime by lazy { (base + offset.minutes) }

    private val deltaTotalMinutesAbs: Int = abs(offset)
    val positive: Boolean get() = offset >= 0
    val deltaHoursAbs: Int get() = deltaTotalMinutesAbs / 60
    val deltaMinutesAbs: Int get() = deltaTotalMinutesAbs % 60

    val timeZone: String by lazy {
        val sign = if (positive) "+" else "-"
        val hour = deltaHoursAbs.padded(2)
        val minute = deltaMinutesAbs.padded(2)
        if (offset == 0) "UTC" else "GMT$sign$hour$minute"
    }

    fun add(deltaMonths: Int, deltaMilliseconds: Double): DateTimeWithOffset = DateTimeWithOffset(base.add(deltaMonths, deltaMilliseconds), offset)
    override fun hashCode(): Int = adjusted.hashCode()
    override fun equals(other: Any?): Boolean = other is DateTimeWithOffset && this.adjusted.unixDouble == other.adjusted.unixDouble
    override fun compareTo(other: DateTimeWithOffset): Int = this.adjusted.unixMillis.compareTo(other.adjusted.unixMillis)
    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
