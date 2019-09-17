package com.soywiz.klock

data class DateTimeRangeSet(val ranges: List<DateTimeRange>) {
    constructor(range: DateTimeRange) : this(listOf(range))
    constructor(vararg ranges: DateTimeRange) : this(ranges.toList())

    operator fun plus(range: DateTimeRange): DateTimeRangeSet = this + DateTimeRangeSet(range)
    operator fun minus(range: DateTimeRange): DateTimeRangeSet = this - DateTimeRangeSet(range)
    fun intersection(range: DateTimeRange): DateTimeRangeSet = this.intersection(DateTimeRangeSet(range))
    fun intersection(vararg range: DateTimeRange): DateTimeRangeSet = this.intersection(DateTimeRangeSet(*range))
    operator fun plus(right: DateTimeRangeSet): DateTimeRangeSet = DateTimeRangeSet(this.ranges + right.ranges).combined()

    // @TODO: Optimize
    operator fun minus(rangeSet: DateTimeRangeSet): DateTimeRangeSet {
        val rightList = rangeSet.combined().ranges
        var out = this.ranges.toList()
        restart@ while (true) {
            for ((leftIndex, left) in out.withIndex()) {
                for (right in rightList) {
                    val result = left.without(right)
                    if (result.size != 1) {
                        out = out.slice(0 until leftIndex) + result + out.slice(leftIndex + 1 until out.size)
                        continue@restart
                    }
                }
            }
            break
        }
        return DateTimeRangeSet(out)
    }

    // @TODO: Optimize
    fun intersection(right: DateTimeRangeSet): DateTimeRangeSet {
        val leftList = this.combined().ranges
        val rightList = right.combined().ranges
        val out = arrayListOf<DateTimeRange>()
        for (l in leftList) {
            val chunks = rightList.mapNotNull { r -> l.intersectionWith(r) }
            out.addAll(DateTimeRangeSet(chunks).combined().ranges)
        }
        return DateTimeRangeSet(out).combined()
    }

    fun combined(): DateTimeRangeSet {
        //return slowCombine()
        return fastCombine()
    }

    // @TODO: Verify this works fine
    internal fun fastCombine(): DateTimeRangeSet {
        val sorted = ranges.sortedBy { it.from.unixMillis }
        val out = arrayListOf<DateTimeRange>()
        var pivot = sorted.first()
        for (n in 1 until sorted.size) {
            val current = sorted[n]
            val result = pivot.mergeOnIntersectionOrNull(current)
            pivot = if (result != null) {
                result
            } else {
                out.add(pivot)
                current
            }
        }
        return DateTimeRangeSet(out + listOf(pivot))
    }

    //internal fun slowCombine(): DateTimeRangeSet {
    //    // @TODO: Improve performance and verify fast combiner
    //    val ranges = this.ranges.toMutableList()
    //    restart@ while (true) {
    //        for (i in ranges.indices) {
    //            for (j in ranges.indices) {
    //                if (i == j) continue
    //                val ri = ranges[i]
    //                val rj = ranges[j]
    //                val concat = ri.mergeOnIntersectionOrNull(rj)
    //                if (concat != null) {
    //                    //println("Combining $ri and $rj : $concat")
    //                    ranges.remove(rj)
    //                    ranges[i] = concat
    //                    continue@restart
    //                }
    //            }
    //        }
    //        break
    //    }
    //    return DateTimeRangeSet(ranges)
    //}

    fun toStringLongs(): String = "${ranges.map { it.toStringLongs() }}"
    override fun toString(): String = "[$ranges]"
}
