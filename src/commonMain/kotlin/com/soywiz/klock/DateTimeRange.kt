package com.soywiz.klock

/**
 * Represents an open or close range between two dates.
 */
data class DateTimeRange(val from: DateTime, val to: DateTime, val inclusive: Boolean) {
    /**
     * Duration [TimeSpan] without having into account actual months/years.
     */
    val duration: TimeSpan by lazy { to - from }

    /**
     * [DateTimeSpan] distance between two dates, month and year aware.
     */
    val span: DateTimeSpan by lazy {
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

    /**
     * Checks if a date is contained in this range.
     */
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

/**
 * Generates a closed range between two [DateTime]
 */
operator fun DateTime.rangeTo(other: DateTime) = DateTimeRange(this, other, inclusive = true)

/**
 * Generates a open range from the right between two [DateTime].
 */
infix fun DateTime.until(other: DateTime) = DateTimeRange(this, other, inclusive = false)
