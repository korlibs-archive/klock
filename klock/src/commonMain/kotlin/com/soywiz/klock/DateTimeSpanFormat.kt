package com.soywiz.klock

interface DateTimeSpanFormat {
    fun format(dd: DateTimeSpan): String
    fun tryParse(str: String, doThrow: Boolean): DateTimeSpan?
}
