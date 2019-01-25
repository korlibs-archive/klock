package com.soywiz.klock

/** Allows to [format] and [parse], [DateTime] and [DateTimeTz] instances */
interface DateFormat {
    fun format(dd: DateTimeTz): String
    fun tryParse(str: String, doThrow: Boolean = false): DateTimeTz?

    companion object {
        val DEFAULT_FORMAT by lazy { DateFormat("EEE, dd MMM yyyy HH:mm:ss z") }
        val FORMAT1 by lazy { DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX") }

        val FORMAT_DATE by lazy { DateFormat("yyyy-MM-dd") }

        val FORMATS = listOf(DEFAULT_FORMAT, FORMAT1)

        fun parse(date: String): DateTimeTz {
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

fun DateFormat.parse(str: String): DateTimeTz =
    tryParse(str, doThrow = true) ?: throw DateException("Not a valid format: '$str' for '$this'")

fun DateFormat.format(date: Double): String = format(DateTime.fromUnix(date))
fun DateFormat.format(date: Long): String = format(DateTime.fromUnix(date))

fun DateFormat.format(dd: DateTime): String = format(dd.toOffsetUnadjusted(0.minutes))
