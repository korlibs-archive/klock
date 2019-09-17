package com.soywiz.klock

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeRangeSetTest {
    @Test
    fun test() {
        val date = DateTime.EPOCH
        fun range(from: Int, to: Int) = (date + from.milliseconds)..(date + to.milliseconds)
        val range1 = range(0, 10)
        val range2 = range(20, 30)
        val range3 = range(30, 40)

        val ranges = DateTimeRangeSet(range1, range2, range3)
        val rangesCombined = ranges.combined()
        assertEquals("[0..10, 20..30, 30..40]", ranges.toStringLongs())
        assertEquals("[0..10, 20..40]", rangesCombined.toStringLongs())
        assertEquals("[0..10, 30..40]", (rangesCombined - range(10, 30)).toStringLongs())
        assertEquals("[0..5, 30..40]", (rangesCombined - range(5, 30)).toStringLongs())
        assertEquals("[5..10, 20..35]", (rangesCombined.intersection(range(5, 35))).toStringLongs())
        assertEquals("[5..10, 20..35]", (rangesCombined.intersection(range(5, 15), range(16, 35))).toStringLongs())
    }

    //@Test
    //fun test2() {
    //    val date = DateTime.EPOCH
    //    val random = Random(0L)
    //    fun range(from: Int, to: Int) = (date + from.milliseconds) .. (date + to.milliseconds)
    //    fun randomRange(): DateTimeRange {
    //        val a = random.nextInt(1000000)
    //        val b = random.nextInt(1000000)
    //        return range(min(a, b), max(a, b))
    //    }
    //    val ranges = (0 until 10000).map { randomRange() }
    //    val ranges2 = DateTimeRangeSet(ranges)
    //    assertEquals(ranges2.fastCombine(), ranges2.slowCombine())
    //    //assertEquals(ranges2.fastCombine(), ranges2.fastCombine())
    //}
}