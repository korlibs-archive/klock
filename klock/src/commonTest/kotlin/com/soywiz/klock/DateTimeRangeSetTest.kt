package com.soywiz.klock

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeRangeSetTest {
    val date = DateTime.EPOCH
    fun range(from: Int, to: Int) = (date + from.milliseconds)..(date + to.milliseconds)

    @Test
    fun test() {
        val range1 = range(0, 10)
        val range2 = range(20, 30)
        val range3 = range(30, 40)

        val rangesList = listOf(range1, range2, range3)
        val ranges = DateTimeRangeSet(rangesList)
        assertEquals("[0..10, 20..30, 30..40]", DateTimeRangeSet.toStringLongs(rangesList))
        assertEquals("[0..10, 20..40]", ranges.toStringLongs())
        assertEquals("[0..10, 30..40]", (ranges - range(10, 30)).toStringLongs())
        assertEquals("[0..5, 30..40]", (ranges - range(5, 30)).toStringLongs())
        assertEquals("[5..10, 20..35]", (ranges.intersection(range(5, 35))).toStringLongs())
        assertEquals("[5..10, 20..35]", (ranges.intersection(range(5, 15), range(16, 35))).toStringLongs())
    }

    private fun randomRanges(count: Int, min: Int = 0, max: Int = 1000000, seed: Long = 0L): List<DateTimeRange> {
        val random = Random(seed)
        fun randomRange(): DateTimeRange {
            val a = random.nextInt(min, max)
            val b = random.nextInt(min, max)
            return range(min(a, b), max(a, b))
        }
        return (0 until count).map { randomRange() }
    }

    private fun randomRangesSeparated(count: Int = 30, seed: Long = 0L): DateTimeRangeSet {
        val random = Random(seed)
        val out = arrayListOf<DateTimeRange>()
        var pos = random.nextLong(1000)
        for (n in 0 until count) {
            val dist = random.nextLong(1000)
            out += DateTimeRange(date + (pos).milliseconds, date + (pos + dist).milliseconds, false)
            val separation = random.nextLong(1000)
            pos += dist + separation
        }
        return DateTimeRangeSet(out)
    }

    @Test
    fun testCombine() {
        val ranges = randomRanges(10000, seed = 0L)
        assertEquals(DateTimeRangeSet.Fast.combine(ranges), DateTimeRangeSet.Slow.combine(ranges))
    }

    @Test
    fun testMinus() {
        val r1 = randomRangesSeparated(40, seed = 10L)
        val r2 = randomRangesSeparated(10, seed = 1L)

        val fast = DateTimeRangeSet.Fast.minus(r1, r2).toStringLongs()
        val slow = DateTimeRangeSet.Slow.minus(r1, r2).toStringLongs()
        println(r1)
        println(r2)
        println("fast: $fast")
        println("slow: $slow")
        assertEquals(fast, slow)
    }

    @Test
    fun testIntersect() {
        //val r1 = randomRangesSeparated(40, seed = 10L)
        //val r2 = randomRangesSeparated(10, seed = 1L)

        for (seed in listOf(1L, 3L, 10L, 100L, 1000L)) {

            //val r1 = randomRangesSeparated(1000, seed = seed)
            //val r2 = randomRangesSeparated(1010, seed = seed + 1)
            val r1 = randomRangesSeparated(100, seed = seed)
            val r2 = randomRangesSeparated(115, seed = seed + 1)

            //val r1 = randomRangesSeparated(3, seed = 10L)
            //val r2 = randomRangesSeparated(3, seed = 1L)

            val fast = DateTimeRangeSet.Fast.intersection(r1, r2).toStringLongs()
            val slow = DateTimeRangeSet.Slow.intersection(r1, r2).toStringLongs()
            //val slow = DateTimeRangeSet.Fast.intersection(r1, r2).toStringLongs()
            println(r1)
            println(r2)
            println("fast: $fast")
            println("slow: $slow")
            assertEquals(fast, slow)

            val fast2 = DateTimeRangeSet.Fast.intersection(r2, r1).toStringLongs()
            val slow2 = DateTimeRangeSet.Slow.intersection(r2, r1).toStringLongs()

            assertEquals(fast2, slow2)
        }
    }
}