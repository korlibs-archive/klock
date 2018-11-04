package com.soywiz.klock

interface DateFormat {
    fun format(dd: DateTimeWithOffset): String
    fun tryParse(str: String, doThrow: Boolean = false): DateTimeWithOffset?

    companion object {
        val DEFAULT_FORMAT by lazy { DateFormat("EEE, dd MMM yyyy HH:mm:ss z") }
        val FORMAT1 by lazy { DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") }

        val FORMAT_DATE by lazy { DateFormat("yyyy-MM-dd") }

        val FORMATS = listOf(DEFAULT_FORMAT, FORMAT1)

        fun parse(date: String): DateTimeWithOffset {
            var lastError: Throwable? = null
            for (format in FORMATS) {
                try {
                    return format.parse(date)
                } catch (e: Throwable) {
                    lastError = e
                }
            }
            throw lastError!!
        }

        operator fun invoke(pattern: String) = PatternDateFormat(pattern)
    }
}

fun DateFormat.parse(str: String): DateTimeWithOffset =
    tryParse(str, doThrow = true) ?: throw DateException("Not a valid format: '$str' for '$this'")

fun DateFormat.format(date: Double): String = format(DateTime.fromUnix(date))
fun DateFormat.format(date: Long): String = format(DateTime.fromUnix(date))

fun DateFormat.format(dd: DateTime): String = format(dd.toOffsetBase(0))
