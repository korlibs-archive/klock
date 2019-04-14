package com.soywiz.klock

interface TimeFormat {
    fun format(dd: TimeSpan): String
    fun tryParse(str: String, doThrow: Boolean): TimeSpan?
}

fun TimeFormat.parse(str: String): TimeSpan =
    tryParse(str, doThrow = true) ?: throw DateException("Not a valid format: '$str' for '$this'")
