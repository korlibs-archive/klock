package com.soywiz.klock

import kotlin.test.*

class DateRangeTest {
    val date1 = DateTime(2018, Month.December, 24)
    val date2 = DateTime(2018, Month.November, 26)
    val date3 = DateTime(2017, Month.November, 26)
    val date4 = DateTime(1, Month.November, 26)

    val christmas = DateTime(2018, Month.December, 25)

    @Test
    fun test() {
        val src = DateTime(2000, Month.January, 10)
        val dst = DateTime(2000, Month.February, 20)
        val other = DateTime(2000, Month.February, 1)

        val outside1 = DateTime(2000, Month.January, 9)
        val outside2 = DateTime(2000, Month.February, 21)

        assertTrue(other in (src .. dst))
        assertTrue(src in (src .. dst))
        assertTrue(dst in (src .. dst))

        assertTrue(src in (src until dst))
        assertTrue(dst !in (src until dst))

        assertTrue(outside1 !in (src .. dst))
        assertTrue(outside2 !in (src .. dst))
        assertTrue(outside1 !in (src until dst))
        assertTrue(outside2 !in (src until dst))
    }

    @Test
    fun testDaysUntilChristmas() {
        assertEquals("1D", (date1 until christmas).span.toString())
        assertEquals("29D", (date2 until christmas).span.toString(includeWeeks = false))
        assertEquals("1Y 29D", (date3 until christmas).span.toString(includeWeeks = false))
        assertEquals("2017Y 29D", (date4 until christmas).span.toString(includeWeeks = false))
    }

    @Test
    fun testDaysUntilChristmasRev() {
        assertEquals("-1D", (christmas until date1).span.toString())
        assertEquals("-29D", (christmas until date2).span.toString(includeWeeks = false))
        assertEquals("-1Y -29D", (christmas until date3).span.toString(includeWeeks = false))
        assertEquals("-2017Y -29D", (christmas until date4).span.toString(includeWeeks = false))
    }
}
