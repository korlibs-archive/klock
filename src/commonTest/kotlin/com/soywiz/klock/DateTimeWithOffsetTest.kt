package com.soywiz.klock

import kotlin.test.*

class DateTimeWithOffsetTest {
    val date1 = (DateTime(2018, 3, 2, 1) + 100.seconds)
    val date2 = (DateTime(2018, 3, 2, 1) + 100.seconds + 60.minutes)

    @Test
    fun test1() {
        assertEquals(date1.toOffsetBase(+60), date1.toOffsetBase(+60))
    }

    @Test
    fun test2() {
        assertEquals(date1.toOffsetBase(-60), date2.toOffsetBase(+0))
    }

    @Test
    fun test4() {
        assertNotEquals(date1, date2)
    }

    @Test
    fun test5() {
        assertTrue(date1.toOffsetBase(0.minutes) > date1.toOffsetBase(+60.minutes))
        assertTrue(date1.toOffsetBase(0.minutes) < date1.toOffsetBase(-60.minutes))
    }

    @Test
    fun test6() {
        assertEquals(date1.toOffsetBase(0.minutes), date1.toOffsetBase(+0.minutes))
    }

    @Test
    fun test7() {
        assertEquals(date1.toOffsetAdjusted(+60.minutes), date1.toOffsetAdjusted(+0.minutes))
        assertTrue(date1.toOffsetBase(-60.minutes) > date1.toOffsetBase(+0.minutes))
    }

    @Test
    fun test8() {
        assertEquals(
            DateTime(2018, Month.March, 2).toOffsetBase(+60.minutes),
            DateTime(2018, Month.February, 2).toOffsetBase(+60.minutes) + 1.months
        )
    }

    @Test
    fun test9() {
        val format = "yyyy-MM-dd HH:mm:ss z"
        val date = DateTimeWithOffset(DateTime(2018, Month.March, 2), offset = 60.minutes.offset)
        assertEquals("2018-03-02 00:00:00 GMT+0100", date.toString(format))
        assertEquals("2018-03-02 00:00:00 GMT+0300", date.addOffset(120).toString(format))
    }
}
