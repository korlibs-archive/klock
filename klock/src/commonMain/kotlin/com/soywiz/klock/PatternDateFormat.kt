package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.jvm.JvmOverloads
import kotlin.math.*

data class PatternDateFormat @JvmOverloads constructor(val format: String, val locale: KlockLocale? = null, val tzNames: TimezoneNames = TimezoneNames.DEFAULT, val options: Options = Options.DEFAULT) : DateFormat {
    val realLocale get() = locale ?: KlockLocale.default

    data class Options(val optionalSupport: Boolean = false) {
        companion object {
            val DEFAULT = Options(optionalSupport = false)
            val WITH_OPTIONAL = Options(optionalSupport = true)
        }
    }

    fun withLocale(locale: KlockLocale?) = this.copy(locale = locale)
	fun withTimezoneNames(tzNames: TimezoneNames) = this.copy(tzNames = this.tzNames + tzNames)
    fun withOptions(options: Options) = this.copy(options = options)
    fun withOptional() = this.copy(options = options.copy(optionalSupport = true))
    fun withNonOptional() = this.copy(options = options.copy(optionalSupport = false))

    private val openOffsets by lazy { LinkedHashMap<Int, Int>() }
    private val closeOffsets by lazy { LinkedHashMap<Int, Int>() }

	internal val chunks by lazy {
		arrayListOf<String>().also { chunks ->
			val s = MicroStrReader(format)
			while (s.hasMore) {
				if (s.peekChar() == '\'') {
					val escapedChunk = s.readChunk {
						s.tryRead('\'')
						while (s.hasMore && s.readChar() != '\'') Unit
					}
					chunks.add(escapedChunk)
					continue
				}
                if (options.optionalSupport) {
                    val offset = chunks.size
                    if (s.tryRead('[')) {
                        openOffsets.increment(offset)
                        continue
                    }
                    if (s.tryRead(']')) {
                        closeOffsets.increment(offset - 1)
                        continue
                    }
                }
				val chunk = s.readChunk {
					val c = s.readChar()
					while (s.hasMore && s.tryRead(c)) Unit
				}
				chunks.add(chunk)
			}
		}.toList()
	}

	internal val regexChunks by lazy {
		chunks.map {
			when (it) {
				"E", "EE", "EEE", "EEEE", "EEEEE", "EEEEEE" -> """(\w+)"""
				"z", "zzz" -> """([\w\s\-\+\:]+)"""
				"d" -> """(\d{1,2})"""
				"dd" -> """(\d{2})"""
				"M" -> """(\d{1,5})"""
				"MM" -> """(\d{2})"""
				"MMM", "MMMM", "MMMMM" -> """(\w+)"""
				"y" -> """(\d{1,5})"""
				"yy" -> """(\d{2})"""
				"yyy" -> """(\d{3})"""
				"yyyy" -> """(\d{4})"""
				"YYYY" -> """(\d{4})"""
				"H" -> """(\d{1,2})"""
				"HH" -> """(\d{2})"""
				"h" -> """(\d{1,2})"""
				"hh" -> """(\d{2})"""
				"m" -> """(\d{1,2})"""
				"mm" -> """(\d{2})"""
				"s" -> """(\d{1,2})"""
				"ss" -> """(\d{2})"""
				"S" -> """(\d{1,6})"""
				"SS" ->  """(\d{2})"""
				"SSS" -> """(\d{3})"""
				"SSSS" -> """(\d{4})"""
				"SSSSS" -> """(\d{5})"""
				"SSSSSS" -> """(\d{6})"""
				"X", "XX", "XXX", "x", "xx", "xxx" -> """([\w:\+\-]+)"""
				"a" -> """(\w+)"""
				" " -> """(\s+)"""
				else -> when {
					it.startsWith('\'') -> "(" + Regex.escapeReplacement(it.substr(1, it.length - 2)) + ")"
					else -> "(" + Regex.escapeReplacement(it) + ")"
				}
			}
		}
	}

	//val escapedFormat = Regex.escape(format)
	internal val rx2: Regex by lazy { Regex("^" + regexChunks.mapIndexed { index, it ->
        if (options.optionalSupport) {
            val opens = openOffsets.getOrElse(index) { 0 }
            val closes = closeOffsets.getOrElse(index) { 0 }
            buildString {
                repeat(opens) { append("(?:") }
                append(it)
                repeat(closes) { append(")?") }
            }
        } else {
            it
        }
    }.joinToString("") + "$") }


	// EEE, dd MMM yyyy HH:mm:ss z -- > Sun, 06 Nov 1994 08:49:37 GMT
    // YYYY-MM-dd HH:mm:ss

    override fun format(dd: DateTimeTz): String {
        val utc = dd.local
        var out = ""
        for (name in chunks) {
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
                else -> when {
					name.startsWith('\'') -> name.substring(1, name.length - 1)
					else -> name
				}
            }
        }
        return out
    }

	private fun parseError(message: String, str: String): DateTimeTz? {
		println("Parser error: $message, $str, $rx2")
		return null
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
        val result = rx2.find(str) ?: return parseError("Not match", str)
        for ((name, value) in chunks.zip(result.groupValues.drop(1))) {
            if (value.isEmpty()) continue

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
