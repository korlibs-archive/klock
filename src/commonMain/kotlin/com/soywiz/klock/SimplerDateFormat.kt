package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

class SimplerDateFormat(val format: String) {
    companion object {
        private val rx = Regex("""('[\w]+'|[\w]+\B[^Xx]|[Xx]{1,3}|[\w]+)""")
        private val englishDaysOfWeek = listOf(
            "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
        )
        private val englishMonths = listOf(
            "january", "february", "march", "april", "may", "june",
            "july", "august", "september", "october", "november", "december"
        )
        private val englishMonths3 = englishMonths.map { it.substr(0, 3) }

        val DEFAULT_FORMAT by lazy { SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z") }
        val FORMAT1 by lazy { SimplerDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") }

        val FORMATS = listOf(DEFAULT_FORMAT, FORMAT1)

        fun parse(str: String): DateTime {
            var lastError: Throwable? = null
            for (format in FORMATS) {
                try {
                    return format.parseDate(str)
                } catch (e: Throwable) {
                    lastError = e
                }
            }
            throw lastError!!
        }
    }

    private val parts = arrayListOf<String>()
    //val escapedFormat = Regex.escape(format)
    private val escapedFormat = Regex.escapeReplacement(format)

    private val rx2: Regex = Regex("^" + escapedFormat.replace(rx) { result ->
        val v = result.groupValues[0]
        parts += v
        if (v.startsWith("'")) {
            "(" + Regex.escapeReplacement(v.trim('\'')) + ")"
        } else if (v.startsWith("X", ignoreCase = true)) {
            """([Z]|[+-]\d\d|[+-]\d\d\d\d|[+-]\d\d:\d\d)?"""
        } else {
            """([\w\+\-]*[^Z+-\.])"""
        }
    } + "$")

    private val parts2 = escapedFormat.splitKeep(rx)

    // EEE, dd MMM yyyy HH:mm:ss z -- > Sun, 06 Nov 1994 08:49:37 GMT
    // YYYY-MM-dd HH:mm:ss

    fun format(date: Double): String = format(DateTime.fromUnix(date))
    fun format(date: Long): String = format(DateTime.fromUnix(date))

    fun format(dd: DateTime): String {
        var out = ""
        for (name2 in parts2) {
            val name = name2.trim('\'')
            out += when (name) {
                "EEE" -> englishDaysOfWeek[dd.dayOfWeek.index0].substr(0, 3).capitalize()
                "EEEE" -> englishDaysOfWeek[dd.dayOfWeek.index0].capitalize()
                "EEEEE" -> englishDaysOfWeek[dd.dayOfWeek.index0].substr(0, 1).capitalize()
                "EEEEEE" -> englishDaysOfWeek[dd.dayOfWeek.index0].substr(0, 2).capitalize()
                "z", "zzz" -> dd.timeZone
                "d" -> dd.dayOfMonth.toString()
                "dd" -> dd.dayOfMonth.padded(2)
                "M" -> dd.month1.padded(1)
                "MM" -> dd.month1.padded(2)
                "MMM" -> englishMonths[dd.month0].substr(0, 3).capitalize()
                "MMMM" -> englishMonths[dd.month0].capitalize()
                "MMMMM" -> englishMonths[dd.month0].substr(0, 1).capitalize()
                "y" -> dd.year
                "yy" -> (dd.year % 100).padded(2)
                "yyy" -> (dd.year % 1000).padded(3)
                "yyyy" -> dd.year.padded(4)
                "YYYY" -> dd.year.padded(4)
                "H" -> dd.hours.padded(1)
                "HH" -> dd.hours.padded(2)
                "h" -> (((12 + dd.hours) % 12)).padded(1)
                "hh" -> (((12 + dd.hours) % 12)).padded(2)
                "m" -> dd.minutes.padded(1)
                "mm" -> dd.minutes.padded(2)
                "s" -> dd.seconds.padded(1)
                "ss" -> dd.seconds.padded(2)
                "S", "SS", "SSS", "SSSS", "SSSSS", "SSSSSS" -> {
                    val milli = dd.milliseconds
                    val base10length = log10(dd.milliseconds.toDouble()).toInt() + 1
                    if (base10length > name.length) {
                        val fractionalPart = (milli.toDouble() * 10.0.pow(-1 * (base10length - name.length))).toInt()
                        fractionalPart
                    } else {
                        var fractionalPart = "${milli.padded(3)}000"
                        fractionalPart.substr(0, name.length)
                    }
                }
                "X", "XX", "XXX", "x", "xx", "xxx" -> when {
                    name.startsWith("X") && dd.offset == 0 -> "Z"
                    else -> {
                        val p = if (dd.offset >= 0) "+" else "-"
                        val hours = dd.offset / 60
                        val minutes = dd.offset % 60
                        when (name) {
                            "X", "x" -> "$p${hours.padded(2)}"
                            "XX", "xx" -> "$p${hours.padded(2)}${minutes.padded(2)}"
                            "XXX", "xxx" -> "$p${hours.padded(2)}:${minutes.padded(2)}"
                            else -> name
                        }
                    }
                }
                "a" -> if (dd.hours < 12) "am" else "pm"
                else -> name
            }
        }
        return out
    }

    fun parse(str: String): Double = parseDate(str).unixDouble
    fun parseUtc(str: String): Double = parseDate(str).utc.unixDouble

    fun parseLong(str: String): Long = parseDate(str).unixLong
    fun parseUtcLong(str: String): Long = parseDate(str).utc.unixLong

    fun parseOrNull(str: String?): Double? = try {
        str?.let { parse(str) }
    } catch (e: Throwable) {
        null
    }

    fun parseOrNullLong(str: String?): Long? = try {
        str?.let { parse(str).toLong() }
    } catch (e: Throwable) {
        null
    }

    fun parseDate(str: String): DateTime = tryParseDate(str) ?: throw DateException("Not a valid format: '$str' for '$format'")

    fun tryParseDate(str: String): DateTime? {
        var millisecond = 0
        var second = 0
        var minute = 0
        var hour = 0
        var day = 1
        var month = 1
        var fullYear = 1970
        var offset: Int? = null
        var isPm = false
        var is12HourFormat = false
        val result = rx2.find(str) ?: return null
        for ((name, value) in parts.zip(result.groupValues.drop(1))) {
            when (name) {
                "EEE", "EEEE" -> Unit // day of week (Sun | Sunday)
                "z", "zzz" -> Unit // timezone (GMT)
                "d", "dd" -> day = value.toInt()
                "M", "MM" -> month = value.toInt()
                "MMM" -> month = englishMonths3.indexOf(value.toLowerCase()) + 1
                "y", "yyyy", "YYYY" -> fullYear = value.toInt()
                "yy" -> throw RuntimeException("Not guessing years from two digits.")
                "yyy" -> fullYear = value.toInt() + if (value.toInt() < 800) 2000 else 1000 // guessing year...
                "H", "HH" -> hour = value.toInt()
                "m", "mm" -> minute = value.toInt()
                "s", "ss" -> second = value.toInt()
                "S", "SS", "SSS", "SSSS", "SSSSS", "SSSSSS" -> {
                    val base10length = log10(value.toDouble()).toInt() + 1
                    millisecond = if (base10length > 3) {
                        // only precision to millisecond supported, ignore the rest. ex: 9999999 => 999"
                        (value.toDouble() * 10.0.pow(-1 * (base10length - 3))).toInt()
                    } else {
                        value.toInt()
                    }
                }
                "X", "XX", "XXX", "x", "xx", "xxx" -> when {
                    name.startsWith("X") && value.first() == 'Z' -> offset = 0
                    name.startsWith("x") && value.first() == 'Z' -> {
                        throw RuntimeException("Zulu Time Zone is only accepted with X-XXX formats.")
                    }
                    value.first() != 'Z' -> {
                        val hours = value.drop(1).substringBefore(':').toInt()
                        val minutes = value.substringAfter(':', "0").toInt()
                        offset = (hours * 60) + minutes
                        if (value.first() == '-') {
                            offset = -offset
                        }
                    }
                }
                "MMMM" -> month = englishMonths.indexOf(value.toLowerCase()) + 1
                "MMMMM" -> throw RuntimeException("Not possible to get the month from one letter.")
                "h", "hh" -> {
                    hour = value.toInt()
                    is12HourFormat = true
                }
                "a" -> isPm = value == "pm"
                else -> {
                    // ...
                }
            }
        }
        //return DateTime.createClamped(fullYear, month, day, hour, minute, second)
        if (is12HourFormat and isPm) hour += 12
        val dateTime = DateTime.createAdjusted(fullYear, month, day, hour, minute, second, millisecond)
        return when (offset) {
            null -> dateTime
            0 -> dateTime.toUtc()
            else -> dateTime.minus(offset.minutes).toOffset(offset)
        }
    }
}
