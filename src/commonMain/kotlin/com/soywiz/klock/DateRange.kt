package com.soywiz.klock

data class DateRange(val from: DateTime, val to: DateTime, val inclusive: Boolean) {
    operator fun contains(date: DateTime): Boolean {
        val unix = date.unixDouble
        val from = from.unixDouble
        val to = to.unixDouble
        if (unix < from) return false
        return when {
            inclusive -> unix <= to
            else -> unix < to
        }
    }
}

operator fun DateTime.rangeTo(other: DateTime) = DateRange(this, other, inclusive = true)
infix fun DateTime.until(other: DateTime) = DateRange(this, other, inclusive = false)
