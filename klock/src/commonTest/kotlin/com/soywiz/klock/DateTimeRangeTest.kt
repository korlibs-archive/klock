package com.soywiz.klock

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTimeRangeTest {
    val format = ISO8601.DATETIME_COMPLETE
    fun String.date() = format.parseUtc(this)

    @Test
    fun test() {
        val range1 = "2019-09-17T13:53:31".date().."2019-10-17T07:00:00".date()
        val range2 = "2019-09-17T14:53:31".date().."2019-10-17T08:00:00".date()
        val range3 = "2019-10-19T00:00:00".date().."2019-10-20T00:00:00".date()
        assertEquals("2019-09-17T14:53:31..2019-10-17T07:00:00", range1.intersectionWith(range2)?.toString(format))
        assertEquals(null, range1.intersectionWith(range3)?.toString(format))
    }
}
