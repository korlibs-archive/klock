package com.soywiz.klock

/**
 * Represents an open or close range between two dates.
 */
data class DateTimeRange(val from: DateTime, val to: DateTime, val inclusive: Boolean) {
    val min get() = from
    val max get() = to
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
        val unix = date.unixMillisDouble
        val from = from.unixMillisDouble
        val to = to.unixMillisDouble
        if (unix < from) return false
        return when {
            inclusive -> unix <= to
            else -> unix < to
        }
    }

    // @TODO: Handle inclusive <= or <
    private inline fun <T> _intersectionWith(that: DateTimeRange, handler: (from: DateTime, to: DateTime, matches: Boolean, inclusive: Boolean) -> T): T {
        val from = max(this.from, that.from)
        val to = min(this.to, that.to)
        return handler(from, to, from <= to, when {
            this.to > that.to -> this.inclusive
            this.to == that.to -> this.inclusive || that.inclusive
            else -> that.inclusive
        })
    }

    fun intersectionWith(that: DateTimeRange): DateTimeRange? {
        return _intersectionWith(that) { from, to, matches, inclusive ->
            when {
                matches -> DateTimeRange(from, to, inclusive)
                else -> null
            }
        }
    }

    fun intersectsWith(that: DateTimeRange): Boolean = _intersectionWith(that) { _, _, matches, _ -> matches }

    fun mergeOnIntersectionOrNull(that: DateTimeRange): DateTimeRange? {
        if (!intersectsWith(that)) return null
        val from = min(this.from, that.from)
        val to = max(this.to, that.to)
        return DateTimeRange(from, to, if (this.to > that.to) this.inclusive else that.inclusive)
    }

    fun without(that: DateTimeRange): List<DateTimeRange> = when {
        // Full remove
        (that.from <= this.from) && (that.to >= this.to) -> listOf()
        // To the right or left, nothing to remove
        that.from >= this.to || that.to <= this.from -> listOf(this)
        // In the middle
        else -> {
            val p0 = this.from
            val p1 = that.from
            val p2 = that.to
            val p3 = this.to
            val c1 = if (p0 < p1) DateTimeRange(p0, p1, inclusive = false) else null
            val c2 = if (p2 < p3) DateTimeRange(p2, p3, inclusive = false) else null
            listOfNotNull(c1, c2)
        }
    }

    fun toString(format: DateFormat): String = "${from.toString(format)}..${to.toString(format)}"
    fun toStringLongs(): String = "${from.unixMillisLong}..${to.unixMillisLong}"
    override fun toString(): String = toString(DateFormat.FORMAT1)
}

/**
 * Generates a closed range between two [DateTime]
 */
operator fun DateTime.rangeTo(other: DateTime) = DateTimeRange(this, other, inclusive = true)

/**
 * Generates a open range from the right between two [DateTime].
 */
infix fun DateTime.until(other: DateTime) = DateTimeRange(this, other, inclusive = false)
