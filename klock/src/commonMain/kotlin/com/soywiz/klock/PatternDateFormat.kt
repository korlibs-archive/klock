package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.jvm.JvmOverloads
import kotlin.math.*

class PatternDateFormat @JvmOverloads constructor(val format: String, val locale: KlockLocale?, val tzNames: TimezoneNames = TimezoneNames.DEFAULT) : DateFormat {
    val realLocale get() = locale ?: KlockLocale.default

    constructor(format: String) : this(format, null)
    companion object {
        private val rx by lazy { Regex("""('[\w]+'|[\w]+\B[^Xx]|[Xx]{1,3}|[\w]+)""") }
    }

    fun withLocale(locale: KlockLocale?) = PatternDateFormat(format, locale)
	fun withTimezoneNames(tzNames: TimezoneNames) = PatternDateFormat(format, locale, this.tzNames + tzNames)

    private val parts = arrayListOf<String>()
    //val escapedFormat = Regex.escape(format)
    private val escapedFormat = Regex.escapeReplacement(format)

    private val rx2: Regex = Regex("^" + escapedFormat.replace(rx) { result ->
        val v = result.groupValues[0]
        parts += v
		when {
			v.startsWith("'") -> "(" + Regex.escapeReplacement(v.trim('\'')) + ")"
			v.startsWith("X", ignoreCase = true) -> """([Z]|[+-]\d\d|[+-]\d\d\d\d|[+-]\d\d:\d\d)?"""
			v.startsWith("Z", ignoreCase = true) -> """([\w\s\-\+\:]+)"""
			v.startsWith("S") -> """(\d+)"""
			else -> """([\w\+\-]*[^Z+-\.])"""
		}
    } + "$")

    private val parts2 = escapedFormat.splitKeep(rx)

    // EEE, dd MMM yyyy HH:mm:ss z -- > Sun, 06 Nov 1994 08:49:37 GMT
    // YYYY-MM-dd HH:mm:ss

    override fun format(dd: DateTimeTz): String {
        val utc = dd.local
        var out = ""
        for (name2 in parts2) {
            val name = name2.trim('\'')
            out += when (name) {
                "E", "EE", "EEE" -> realLocale.daysOfWeekShort[utc.dayOfWeek.index0].capitalize()
                "EEEE", "EEEEE", "EEEEEE" -> realLocale.daysOfWeek[utc.dayOfWeek.index0].capitalize()
                "z", "zzz" -> dd.offset.timeZone
                "d" -> utc.dayOfMonth.toString()
                "dd" -> utc.dayOfMonth.padded(2)
                "M" -> utc.month1.padded(1)
                "MM" -> utc.month1.padded(2)
                "MMM" -> realLocale.months[utc.month0].substr(0, 3).capitalize()
                "MMMM" -> realLocale.months[utc.month0].capitalize()
                "MMMMM" -> realLocale.months[utc.month0].substr(0, 1).capitalize()
                "y" -> utc.yearInt
                "yy" -> (utc.yearInt % 100).padded(2)
                "yyy" -> (utc.yearInt % 1000).padded(3)
                "yyyy" -> utc.yearInt.padded(4)
                "YYYY" -> utc.yearInt.padded(4)
                "H" -> utc.hours.padded(1)
                "HH" -> utc.hours.padded(2)
                "h" -> (((12 + utc.hours) % 12)).padded(1)
                "hh" -> (((12 + utc.hours) % 12)).padded(2)
                "m" -> utc.minutes.padded(1)
                "mm" -> utc.minutes.padded(2)
                "s" -> utc.seconds.padded(1)
                "ss" -> utc.seconds.padded(2)
                "S", "SS", "SSS", "SSSS", "SSSSS", "SSSSSS" -> {
                    val milli = utc.milliseconds
                    val base10length = log10(utc.milliseconds.toDouble()).toInt() + 1
                    if (base10length > name.length) {
                        val fractionalPart = (milli.toDouble() * 10.0.pow(-1 * (base10length - name.length))).toInt()
                        fractionalPart
                    } else {
                        val fractionalPart = "${milli.padded(3)}000"
                        fractionalPart.substr(0, name.length)
                    }
                }
                "X", "XX", "XXX", "x", "xx", "xxx" -> {
                    when {
                        name.startsWith("X") && dd.offset.totalMinutesInt == 0 -> "Z"
                        else -> {
                            val p = if (dd.offset.totalMinutesInt >= 0) "+" else "-"
                            val hours = (dd.offset.totalMinutesInt / 60).absoluteValue
                            val minutes = (dd.offset.totalMinutesInt % 60).absoluteValue
                            when (name) {
                                "X", "x" -> "$p${hours.padded(2)}"
                                "XX", "xx" -> "$p${hours.padded(2)}${minutes.padded(2)}"
                                "XXX", "xxx" -> "$p${hours.padded(2)}:${minutes.padded(2)}"
                                else -> name
                            }
                        }
                    }
                }
                "a" -> if (utc.hours < 12) "am" else "pm"
                else -> name
            }
        }
        return out
    }


    override fun tryParse(str: String, doThrow: Boolean): DateTimeTz? {
        var millisecond = 0
        var second = 0
        var minute = 0
        var hour = 0
        var day = 1
        var month = 1
        var fullYear = 1970
        var offset: TimeSpan? = null
        var isPm = false
        var is12HourFormat = false
        val result = rx2.find(str) ?: return null
        for ((name, value) in parts.zip(result.groupValues.drop(1))) {
            when (name) {
                "E", "EE", "EEE", "EEEE", "EEEEE", "EEEEEE" -> Unit // day of week (Sun | Sunday)
                "z", "zzz" -> { // timezone (GMT)
					val tzOffset = tzNames.namesToOffsets[value.toUpperCase()]
					if (tzOffset != null) {
						offset = tzOffset
					} else {
						var sign = +1
						val reader = MicroStrReader(value)
						reader.tryRead("GMT")
						reader.tryRead("UTC")
						if (reader.tryRead("+")) sign = +1
						if (reader.tryRead("-")) sign = -1
						val part = reader.readRemaining().replace(":", "")
						val hours = part.substr(0, 2).padStart(2, '0').toIntOrNull() ?: 0
						val minutes = part.substr(2, 2).padStart(2, '0').toIntOrNull() ?: 0
						val roffset = hours.hours + minutes.minutes
						offset = if (sign > 0) +roffset else -roffset
					}
				}
                "d", "dd" -> day = value.toInt()
                "M", "MM" -> month = value.toInt()
                "MMM" -> month = realLocale.monthsShort.indexOf(value.toLowerCase()) + 1
                "y", "yyyy", "YYYY" -> fullYear = value.toInt()
                "yy" -> if (doThrow) throw RuntimeException("Not guessing years from two digits.") else return null
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
                    name.startsWith("X") && value.first() == 'Z' -> offset = 0.hours
                    name.startsWith("x") && value.first() == 'Z' -> {
                        if (doThrow) throw RuntimeException("Zulu Time Zone is only accepted with X-XXX formats.") else return null
                    }
                    value.first() != 'Z' -> {
                        val hours = value.drop(1).substringBefore(':').toInt()
                        val minutes = value.substringAfter(':', "0").toInt()
                        offset = hours.hours + minutes.minutes
                        if (value.first() == '-') {
                            offset = -offset
                        }
                    }
                }
                "MMMM" -> month = realLocale.months.indexOf(value.toLowerCase()) + 1
                "MMMMM" -> if (doThrow) throw RuntimeException("Not possible to get the month from one letter.") else return null
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
        if (is12HourFormat && isPm) {
            hour += 12
        }
        val dateTime = DateTime.createAdjusted(fullYear, month, day, hour, minute, second, millisecond)
        return dateTime.toOffsetUnadjusted(offset ?: 0.hours)
    }

    override fun toString(): String = format
}
