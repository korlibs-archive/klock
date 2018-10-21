package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

class OffsetDateTime private constructor(
    override val utc: UtcDateTime,
    override val offset: Int,
    override val adjusted: UtcDateTime = utc.addMinutes(offset.toDouble()) as UtcDateTime
) : DateTime by adjusted, Comparable<DateTime> {
    private val deltaTotalMinutesAbs: Int = abs(offset)
    val positive: Boolean get() = offset >= 0
    val deltaHoursAbs: Int get() = deltaTotalMinutesAbs / 60
    val deltaMinutesAbs: Int get() = deltaTotalMinutesAbs % 60

    companion object {
        //operator fun invoke(utc: DateTime, offset: Int) = OffsetDateTime(utc.utc, utc.offsetTotalMinutes + offset)
        operator fun invoke(utc: DateTime, offset: Int) = OffsetDateTime(utc.utc, offset)
    }

    override val timeZone: String = run {
        val sign = if (positive) "+" else "-"
        val hour = deltaHoursAbs.padded(2)
        val minute = deltaMinutesAbs.padded(2)
        "GMT$sign$hour$minute"
    }

    override fun add(deltaMonths: Int, deltaMilliseconds: Double): DateTime = OffsetDateTime(utc.add(deltaMonths, deltaMilliseconds), offset)
    override fun hashCode(): Int = adjusted.hashCode()
    override fun equals(other: Any?): Boolean = other is DateTime && this.adjusted.unixDouble == other.adjusted.unixDouble
    override fun compareTo(other: DateTime): Int = this.adjusted.unixMillis.compareTo(other.adjusted.unixMillis)
    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
