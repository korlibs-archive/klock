package com.soywiz.klock

interface TimeFormat {
    fun format(dd: TimeSpan): String
    fun tryParse(str: String, doThrow: Boolean): TimeSpan?
}
