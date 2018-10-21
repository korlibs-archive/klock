package com.soywiz.klock

import kotlin.test.*

class DateTimeTestWithOffset {
    val date1 = (DateTime(2018, 3, 2, 1) + 100.seconds)
    val date2 = (DateTime(2018, 3, 2, 1) + 100.seconds + 60.minutes)

    @Test
    fun test1() {
        assertEquals(date1.toOffset(+60), date1.toOffset(+60))
    }

    @Test
    fun test2() {
        assertEquals(date1.toOffset(+60), date2.toOffset(+0))
    }

    @Test
    fun test4() {
        assertNotEquals(date1, date2)
    }

    @Test
    fun test5() {
        assertTrue(date1.toOffset(0) < date1.toOffset(+60))
    }

    @Test
    fun test6() {
        assertEquals(date1.toOffset(0), date1.toOffset(+0))
    }

    @Test
    fun test7() {
        assertTrue(date1.toOffset(+60) > date1.toOffset(+0))
    }
}
