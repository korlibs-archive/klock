package com.soywiz.klock.internal

import com.soywiz.klock.*
import kotlin.math.*

internal const val MILLIS_PER_MICROSECOND = 1.0 / 1000.0
internal const val MILLIS_PER_NANOSECOND = MILLIS_PER_MICROSECOND / 1000.0

internal const val MILLIS_PER_SECOND = 1000
internal const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60
internal const val MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60
internal const val MILLIS_PER_DAY = MILLIS_PER_HOUR * 24
internal const val MILLIS_PER_WEEK = MILLIS_PER_DAY * 7

internal const val DAYS_PER_YEAR = 365
internal const val DAYS_PER_4_YEARS = DAYS_PER_YEAR * 4 + 1
internal const val DAYS_PER_100_YEARS = DAYS_PER_4_YEARS * 25 - 1
internal const val DAYS_PER_400_YEARS = DAYS_PER_100_YEARS * 4 + 1

internal fun Int.padded(count: Int) = this.toString().padStart(count, '0')

internal fun String.substr(start: Int, length: Int): String {
    val low = (if (start >= 0) start else this.length + start).clamp(0, this.length)
    val high = (if (length >= 0) low + length else this.length + length).clamp(0, this.length)
    return if (high < low) "" else this.substring(low, high)
}

internal fun Int.clamp(min: Int, max: Int): Int = if (this < min) min else if (this > max) max else this
internal fun Int.cycle(min: Int, max: Int): Int = ((this - min) umod (max - min + 1)) + min
internal fun Int.cycleSteps(min: Int, max: Int): Int = (this - min) / (max - min + 1)

internal fun String.splitKeep(regex: Regex): List<String> {
    val str = this
    val out = arrayListOf<String>()
    var lastPos = 0
    for (part in regex.findAll(this)) {
        val prange = part.range
        if (lastPos != prange.start) {
            out += str.substring(lastPos, prange.start)
        }
        out += str.substring(prange)
        lastPos = prange.endInclusive + 1
    }
    if (lastPos != str.length) {
        out += str.substring(lastPos)
    }
    return out
}

internal infix fun Int.umod(that: Int): Int {
    val remainder = this % that
    return when {
        remainder < 0 -> remainder + that
        else -> remainder
    }
}

internal class Moduler(var value: Double) {
    fun double(count: Double): Double {
        val ret = (value / count)
        value %= count
        return floor(ret)
    }

    inline fun double(count: Number): Double = double(count.toDouble())
    inline fun int(count: Number): Int = double(count.toDouble()).toInt()
}

internal infix fun Double.intDiv(other: Double) = floor(this / other)

open class _InternalDateTimeCompanion {
    val EPOCH = UtcDateTime(0.0)

    // Can produce errors on invalid dates
    operator fun invoke(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        milliseconds: Int = 0
    ): DateTime = UtcDateTime(
        UtcDateTime.dateToMillis(year, month, day) + UtcDateTime.timeToMillis(hour, minute, second) + milliseconds
    )

    operator fun invoke(time: Long) = fromUnix(time)

    fun fromString(str: String) = SimplerDateFormat.parse(str)
    fun parse(str: String) = SimplerDateFormat.parse(str)

    fun fromUnix(time: Double): UtcDateTime = UtcDateTime(time)
    fun fromUnixLocal(time: Double): OffsetDateTime = UtcDateTime(time).local

    fun fromUnix(time: Long): UtcDateTime = fromUnix(time.toDouble())
    fun fromUnixLocal(time: Long): OffsetDateTime = fromUnixLocal(time.toDouble())

    fun nowUnix() = Klock.currentTimeMillis()
    fun now() = fromUnix(nowUnix())
    fun nowLocal() = fromUnix(nowUnix()).toLocal()

    // Can't produce errors on invalid dates and tries to adjust it to a valid date.
    fun createClamped(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        milliseconds: Int = 0
    ): DateTime {
        val clampedMonth = month.clamp(1, 12)
        return createUnchecked(
            year = year,
            month = clampedMonth,
            day = day.clamp(1, daysInMonth(clampedMonth, year)),
            hour = hour.clamp(0, 23),
            minute = minute.clamp(0, 59),
            second = second.clamp(0, 59),
            milliseconds = milliseconds
        )
    }

    // Can't produce errors on invalid dates and tries to adjust it to a valid date.
    fun createAdjusted(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        milliseconds: Int = 0
    ): DateTime {
        var dy = year
        var dm = month
        var dd = day
        var th = hour
        var tm = minute
        var ts = second

        tm += ts.cycleSteps(0, 59); ts = ts.cycle(0, 59) // Adjust seconds, adding minutes
        th += tm.cycleSteps(0, 59); tm = tm.cycle(0, 59) // Adjust minutes, adding hours
        dd += th.cycleSteps(0, 23); th = th.cycle(0, 23) // Adjust hours, adding days

        while (true) {
            val dup = daysInMonth(dm, dy)

            dm += dd.cycleSteps(1, dup); dd = dd.cycle(1, dup) // Adjust days, adding months
            dy += dm.cycleSteps(1, 12); dm = dm.cycle(1, 12) // Adjust months, adding years

            // We already have found a day that is valid for the adjusted month!
            if (dd.cycle(1, daysInMonth(dm, dy)) == dd) {
                break
            }
        }

        return createUnchecked(dy, dm, dd, th, tm, ts, milliseconds)
    }

    // Can't produce errors on invalid dates
    fun createUnchecked(
        year: Int,
        month: Int,
        day: Int,
        hour: Int = 0,
        minute: Int = 0,
        second: Int = 0,
        milliseconds: Int = 0
    ): DateTime {
        return UtcDateTime(
            UtcDateTime.dateToMillisUnchecked(year, month, day) + UtcDateTime.timeToMillisUnchecked(
                hour,
                minute,
                second
            ) + milliseconds
        )
    }

    fun isLeapYear(year: Int): Boolean = Year.isLeap(year)
    fun daysInMonth(month: Int, isLeap: Boolean): Int = Month.days(month, isLeap)
    fun daysInMonth(month: Int, year: Int): Int = daysInMonth(month, isLeapYear(year))
}