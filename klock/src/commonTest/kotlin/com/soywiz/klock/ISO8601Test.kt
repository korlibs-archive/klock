package com.soywiz.klock

import kotlin.test.Test
import kotlin.test.assertEquals

class ISO8601Test {
    @Test
    fun testDate() {
        val date = DateTime(2019, Month.April, 14)
        assertEquals("2019-04-14", date.format(ISO8601.DATE_CALENDAR_COMPLETE))
        assertEquals("2019-04-14", date.format(ISO8601.DATE_CALENDAR_COMPLETE.extended))
        assertEquals("20190414", date.format(ISO8601.DATE_CALENDAR_COMPLETE.basic))

        assertEquals("2019-04", date.format(ISO8601.DATE_CALENDAR_REDUCED0))
        assertEquals("2019-04", date.format(ISO8601.DATE_CALENDAR_REDUCED0.extended))
        assertEquals("2019-04", date.format(ISO8601.DATE_CALENDAR_REDUCED0.basic))

        assertEquals("2019", date.format(ISO8601.DATE_CALENDAR_REDUCED1))
        assertEquals("2019", date.format(ISO8601.DATE_CALENDAR_REDUCED1.extended))
        assertEquals("2019", date.format(ISO8601.DATE_CALENDAR_REDUCED1.basic))

        assertEquals("19", date.format(ISO8601.DATE_CALENDAR_REDUCED2))
        assertEquals("19", date.format(ISO8601.DATE_CALENDAR_REDUCED2.extended))
        assertEquals("19", date.format(ISO8601.DATE_CALENDAR_REDUCED2.basic))

        assertEquals("2019-W15-7", date.format(ISO8601.DATE_WEEK_COMPLETE))

        assertEquals(date, ISO8601.DATE.parse("2019-04-14").utc)
        assertEquals(date, ISO8601.DATE.parse("2019-W15-7").utc)
    }


    @Test
    fun testWeekOfYear() {
        assertEquals(1, DateTime(2019, Month.January, 1).dayOfYear)

        assertEquals(1, DateTime(2019, Month.January, 1).weekOfYear1)
        assertEquals(1, DateTime(2019, Month.January, 6).weekOfYear1)
        assertEquals(2, DateTime(2019, Month.January, 7).weekOfYear1)
        assertEquals(2, DateTime(2019, Month.January, 13).weekOfYear1)
        assertEquals(3, DateTime(2019, Month.January, 14).weekOfYear1)

        assertEquals(1, DateTime(2018, Month.January, 1).weekOfYear1)
        assertEquals(1, DateTime(2018, Month.January, 7).weekOfYear1)
        assertEquals(2, DateTime(2018, Month.January, 8).weekOfYear1)
        assertEquals(2, DateTime(2018, Month.January, 14).weekOfYear1)
        assertEquals(3, DateTime(2018, Month.January, 15).weekOfYear1)

        assertEquals(1, DateTime(2018, Month.January, 1).weekOfYear1)
        assertEquals(1, DateTime(2018, Month.January, 7).weekOfYear1)
        assertEquals(2, DateTime(2018, Month.January, 8).weekOfYear1)
        assertEquals(2, DateTime(2018, Month.January, 14).weekOfYear1)
        assertEquals(3, DateTime(2018, Month.January, 15).weekOfYear1)

        assertEquals(44, DateTime(2007, Month.November, 3).weekOfYear1)
        assertEquals(6, DateTime(2007, Month.November, 3).dayOfWeek.index1Monday)
    }
}