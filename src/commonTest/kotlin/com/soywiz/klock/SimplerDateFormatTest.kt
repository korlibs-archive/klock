package com.soywiz.klock

import kotlin.test.*

class SimplerDateFormatTest {
    // Sun, 06 Nov 1994 08:49:37 GMT
    val format = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

    @Test
    fun testParse() {
        assertEquals(784111777000, format.parseLong("Sun, 06 Nov 1994 08:49:37 UTC"))
    }

    @Test
    fun testFormat() {
        assertEquals("Sun, 06 Nov 1994 08:49:37 UTC", format.format(784111777000))
    }

    @Test
    fun testParseFormat() {
        val dateStr = "Sun, 06 Nov 1994 08:49:37 UTC"
        assertEquals(dateStr, format.format(format.parse(dateStr)))
    }

    class StrictOffset {
        val format = SimplerDateFormat("yyyy-MM-dd'T'HH:mm:ssxxx")

        @Test
        fun testParseUtc() {
            assertEquals(1462390174000, format.parseLong("2016-05-04T19:29:34+00:00"))
        }

        @Test
        fun testFormatUtc() {
            assertEquals("2016-05-04T19:29:34+00:00", format.format(1462390174000))
        }

        @Test
        fun testParseFormatUtc() {
            val dateStr = "2016-05-04T19:29:34+00:00"
            assertEquals(dateStr, format.format(format.parse(dateStr)))
        }

        @Test
        fun testParseWithOffsetAsUtc() {
            val offsetDateStr = "2016-05-04T19:29:34+05:00"
            val utcDateStr = "2016-05-04T14:29:34+00:00"
            assertEquals(format.parse(utcDateStr), format.parseUtc(offsetDateStr))
        }

        @Test
        fun testParseWithOffset() {
            assertEquals(1462390174000, format.parseLong("2016-05-04T19:29:34-07:00"))
        }

        @Test
        fun testParseFormatOffset() {
            val dateStr = "2016-05-04T19:29:34+05:00"
            assertEquals(dateStr, format.format(format.parseDate(dateStr)))
        }

        @Test
        fun testParseWithZuluFails() {
            val dateStr = "2016-05-04T19:29:34Z"
            assertFailsWith(RuntimeException::class, "Zulu Time Zone is only accepted with X-XXX formats.") {
                format.parse(dateStr)
            }
        }
    }

    class ZuluCapableOffset {
        val format = SimplerDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

        @Test
        fun testParseUtc() {
            assertEquals(1462390174000, format.parseLong("2016-05-04T19:29:34+00:00"))
        }

        @Test
        fun testParseZulu() {
            assertEquals(1462390174000, format.parseLong("2016-05-04T19:29:34Z"))
        }

        @Test
        fun testFormatUtc() {
            assertEquals("2016-05-04T19:29:34Z", format.format(1462390174000))
        }

        @Test
        fun testParseWithUtcOffsetFormatsWithZulu() {
            val dateStr = "2016-05-04T19:29:34+00:00"
            val expectedStr = "2016-05-04T19:29:34Z"
            assertEquals(expectedStr, format.format(format.parse(dateStr)))
        }

        @Test
        fun testParseWithZuluFormatsWithZulu() {
            val dateStr = "2016-05-04T19:29:34Z"
            assertEquals(dateStr, format.format(format.parse(dateStr)))
        }
    }
}
