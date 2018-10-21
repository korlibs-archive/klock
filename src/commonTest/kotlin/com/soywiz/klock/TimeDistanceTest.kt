package com.soywiz.klock

import kotlin.test.*

class TimeDistanceTest {
    @Test
    fun testAddDistance() {
        assertEquals(DateTime(2018, 12, 13), DateTime(2017, 12, 13) + 1.years)
        assertEquals(DateTime(2018, 1, 13), DateTime(2017, 12, 13) + 1.months)
        assertEquals(DateTime(2018, 1, 3), DateTime(2017, 12, 13) + 3.weeks)
        assertEquals(DateTime(2018, 1, 2), DateTime(2017, 12, 13) + 20.days)
        assertEquals(DateTime(2017, 12, 13, 3, 0, 0, 0), DateTime(2017, 12, 13) + 3.hours)
        assertEquals(DateTime(2017, 12, 13, 0, 3, 0, 0), DateTime(2017, 12, 13) + 3.minutes)
        assertEquals(DateTime(2017, 12, 13, 0, 0, 3, 0), DateTime(2017, 12, 13) + 3.seconds)
        assertEquals(DateTime(2017, 12, 13, 0, 0, 0, 3), DateTime(2017, 12, 13) + 3.milliseconds)
    }

    @Test
    fun testToString() {
        assertEquals("1M 10W 10H", (1.months + 10.hours + 10.weeks).toString())
        assertEquals("1M 11W 1D 10H", (1.months + 10.hours + 10.weeks + 8.days).toString())
    }
}
