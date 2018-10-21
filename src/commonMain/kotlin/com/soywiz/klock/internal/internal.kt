package com.soywiz.klock.internal

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
