package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

class OffsetDateTime private constructor(
    override val utc: UtcDateTime,
    override val offset: Int,
    val adjusted: DateTime = utc.addMinutes(offset.toDouble())
) : DateTime by adjusted {
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

    override fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime =
        OffsetDateTime(utc.add(deltaMonths, deltaMilliseconds), offset)

    override fun toUtc(): DateTime = utc

    override fun hashCode(): Int = adjusted.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is DateTime) return false
        val thisMs = this.utc.unixDouble + (this.offset * 1000 * 60)
        val thatMs = other.utc.unixDouble + (other.offset * 1000 * 60)
        return thisMs == thatMs
    }

    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
