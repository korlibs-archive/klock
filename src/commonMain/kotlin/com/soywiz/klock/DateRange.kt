package com.soywiz.klock

data class DateRange(val from: DateTime, val to: DateTime, val inclusive: Boolean) {
    val duration by lazy { to - from }

    val span by lazy {
        val reverse = to < from
        val rfrom = if (!reverse) from else to
        val rto = if (!reverse) to else from

        var years = 0
        var months = 0

        var pivot = rfrom

        // Compute years
        val diffYears = (rto.year - pivot.year)
        pivot += diffYears.years
        years += diffYears
        if (pivot > rto) {
            pivot -= 1.years
            years--
        }

        // Compute months (at most an iteration of 12)
        while (true) {
            val t = pivot + 1.months
            if (t < rto) {
                months++
                pivot = t
            } else {
                break
            }
        }

        val out = DateTimeSpan(years.years + months.months, rto - pivot)
        if (reverse) -out else out
    }

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
