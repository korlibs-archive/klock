package com.soywiz.klock

import kotlin.test.*

class TimeSpanTest {
    @Test
    fun testToTimeString() {
        assertEquals("00:00:01", 1.seconds.toTimeString(components = 3))
        assertEquals("00:01:02", 62.seconds.toTimeString(components = 3))
        assertEquals("01:01:00", 3660.seconds.toTimeString(components = 3))

        assertEquals("00:01", 1.seconds.toTimeString(components = 2))
        assertEquals("01:02", 62.seconds.toTimeString(components = 2))
        assertEquals("61:00", 3660.seconds.toTimeString(components = 2))

        assertEquals("01", 1.seconds.toTimeString(components = 1))
        assertEquals("62", 62.seconds.toTimeString(components = 1))
        assertEquals("3660", 3660.seconds.toTimeString(components = 1))

        assertEquals("01:01:02.500", 3662.5.seconds.toTimeString(components = 3, addMilliseconds = true))
        assertEquals("61:02.500", 3662.5.seconds.toTimeString(components = 2, addMilliseconds = true))
        assertEquals("3662.500", 3662.5.seconds.toTimeString(components = 1, addMilliseconds = true))
    }

    @Test
    fun testOperators() {
        assertEquals((-1).seconds, -(1.seconds))
    }
}
