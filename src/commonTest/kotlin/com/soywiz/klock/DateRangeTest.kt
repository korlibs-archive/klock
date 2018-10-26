package com.soywiz.klock

import kotlin.test.*

class DateRangeTest {
    @Test
    fun test() {
        val from = DateTime(2000, Month.January, 10)
        val to = DateTime(2000, Month.February, 20)
        val other = DateTime(2000, Month.February, 1)

        val outside1 = DateTime(2000, Month.January, 9)
        val outside2 = DateTime(2000, Month.February, 21)

        assertTrue(other in (from .. to))
        assertTrue(from in (from .. to))
        assertTrue(to in (from .. to))

        assertTrue(from in (from until to))
        assertTrue(to !in (from until to))

        assertTrue(outside1 !in (from .. to))
        assertTrue(outside2 !in (from .. to))
        assertTrue(outside1 !in (from until to))
        assertTrue(outside2 !in (from until to))
    }
}
