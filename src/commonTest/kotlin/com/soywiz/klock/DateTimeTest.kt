package com.soywiz.klock

import kotlin.test.*

class DateTimeTest {
	val HttpDate = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z")
	val HttpDate2 = SimplerDateFormat("EEE, dd MMM yyyy H:mm:ss z")

	@Test
	fun testFromString() {
		assertEquals("Mon, 04 Dec 2017 04:35:37 UTC", DateTime.fromString("2017-12-04T04:35:37Z").toString())
	}

	@Test
	fun testFormattingToCustomDateTimeFormats(){
		val dt = DateTime(2018, 9, 8, 4, 8, 9)
		assertEquals("Sat, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("Saturday, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEEE, dd MMM yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("S, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEEEE, dd MMM yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("Sa, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEEEEE, dd MMM yyyy HH:mm:ss z").format(dt).toString())

		assertEquals("Sat, 8 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEE, d MMM yyyy HH:mm:ss z").format(dt).toString())

		assertEquals("Sat, 08 9 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd M yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("Sat, 08 09 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MM yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("Sat, 08 September 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MMMM yyyy HH:mm:ss z").format(dt).toString())
		assertEquals("Sat, 08 S 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MMMMM yyyy HH:mm:ss z").format(dt).toString())

		assertEquals("Sat, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MMM y HH:mm:ss z").format(dt).toString())
		assertEquals("Sat, 08 Sep 2018 04:08:09 UTC", SimplerDateFormat("EEE, dd MMM YYYY HH:mm:ss z").format(dt).toString())

		assertEquals("Sat, 08 Sep 2018 4:08:09 UTC", SimplerDateFormat("EEE, dd MMM yyyy H:mm:ss z").format(dt).toString())

		assertEquals("Sat, 08 Sep 2018 4:08:09 am UTC", SimplerDateFormat("EEE, dd MMM yyyy h:mm:ss a z").format(dt).toString())
		assertEquals("Sat, 08 Sep 2018 04:08:09 am UTC", SimplerDateFormat("EEE, dd MMM yyyy hh:mm:ss a z").format(dt).toString())

		assertEquals("Sat, 08 Sep 2018 04:8:09 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:m:ss z").format(dt).toString())

		assertEquals("Sat, 08 Sep 2018 04:08:9 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s z").format(dt).toString())
	}

    @Test
    fun testFormattingToCustomDateTimeFormatsWithMilliseconds999(){
        val dt = DateTime(2018, 9, 8, 4, 8, 9, 999)
        assertEquals("Sat, 08 Sep 2018 04:08:9.9 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.S z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.99 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.999 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.9990 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.99900 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.999000 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSSSS z").format(dt).toString())
    }

    @Test
    fun testFormattingToCustomDateTimeFormatsWithMilliseconds009(){
        val dt = DateTime(2018, 9, 8, 4, 8, 9, 9)
        assertEquals("Sat, 08 Sep 2018 04:08:9.0 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.S z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.00 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.009 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.0090 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.00900 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSSS z").format(dt).toString())
        assertEquals("Sat, 08 Sep 2018 04:08:9.009000 UTC", SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s.SSSSSS z").format(dt).toString())
    }

	@Test
	fun testParsingDateTimesInCustomStringFormats(){
		val dtmilli = 1536379689000L
		assertEquals(dtmilli, DateTime(2018, 9, 8, 4, 8, 9).unix)
		assertEquals(message = "Sat, 08 Sep 2018 04:08:09 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse("Sat, 08 Sep 2018 04:08:09 UTC"))
		assertEquals(message = "Saturday, 08 Sep 2018 04:08:09 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEEE, dd MMM yyyy HH:mm:ss z").parse("Saturday, 08 Sep 2018 04:08:09 UTC"))
		assertEquals(message = "S, 08 Sep 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEEEE, dd MMM yyyy HH:mm:ss z").parse("S, 08 Sep 2018 04:08:09 UTC"))
		assertEquals(message = "Sa, 08 Sep 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEEEEE, dd MMM yyyy HH:mm:ss z").parse("Sa, 08 Sep 2018 04:08:09 UTC"))

		assertEquals(message = "Sat, 8 Sep 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEE, d MMM yyyy HH:mm:ss z").parse("Sat, 8 Sep 2018 04:08:09 UTC"))

		assertEquals(message = "Sat, 08 9 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd M yyyy HH:mm:ss z").parse("Sat, 08 9 2018 04:08:09 UTC"))
		assertEquals(message = "Sat, 08 09 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MM yyyy HH:mm:ss z").parse("Sat, 08 09 2018 04:08:09 UTC"))
		assertEquals(message = "Sat, 08 September 2018 04:08:09 UTC",
				expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMMM yyyy HH:mm:ss z").parse("Sat, 08 September 2018 04:08:09 UTC"))
		assertEquals(message = "Sat, 08 S 2018 04:08:09 UTC",
				expected = null,
                actual = SimplerDateFormat("EEE, dd MMMMM yyyy HH:mm:ss z").parseOrNull("Sat, 08 S 2018 04:08:09 UTC"))

        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 UTC - y",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM y HH:mm:ss z").parse("Sat, 08 Sep 2018 04:08:09 UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 UTC - YYYY",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM YYYY HH:mm:ss z").parse("Sat, 08 Sep 2018 04:08:09 UTC"))

        assertEquals(message = "Sat, 08 Sep 2018 4:08:09 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy H:mm:ss z").parse("Sat, 08 Sep 2018 4:08:09 UTC"))

        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 am UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:m:ss z").parse("Sat, 08 Sep 2018 04:8:09 UTC"))

        assertEquals(message = "Sat, 08 Sep 2018 04:08:9 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:s z").parse("Sat, 08 Sep 2018 04:08:9 UTC"))
	}

    @Test
    fun testParsingDateTimesInCustomStringFormatsWithAmPm() {
        val amDtmilli = 1536379689000L
        assertEquals(amDtmilli, DateTime(2018, 9, 8, 4, 8, 9).unix)

        val pmDtmilli = 1536422889000L
        assertEquals(pmDtmilli, DateTime(2018, 9, 8, 16, 8, 9).unix)

        assertEquals(message = "Sat, 08 Sep 2018 4:08:09 am UTC",
                expected = amDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy h:mm:ss a z").parse("Sat, 08 Sep 2018 4:08:09 am UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 am UTC",
                expected = amDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy hh:mm:ss a z").parse("Sat, 08 Sep 2018 04:08:09 am UTC"))

        assertEquals(message = "Sat, 08 Sep 2018 4:08:09 pm UTC",
                expected = pmDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy h:mm:ss a z").parse("Sat, 08 Sep 2018 4:08:09 pm UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 pm UTC",
                expected = pmDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy hh:mm:ss a z").parse("Sat, 08 Sep 2018 04:08:09 pm UTC"))
    }

    @Test
    fun testParsingDateTimesWithPmMixedWith24Hourformat() {
        val pmDtmilli = 1536422889000L
        assertEquals(pmDtmilli, DateTime(2018, 9, 8, 16, 8, 9).unix)

        assertEquals(message = "Sat, 08 Sep 2018 4:08:09 pm UTC",
                expected = pmDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy H:mm:ss a z").parse("Sat, 08 Sep 2018 16:08:09 pm UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09 pm UTC",
                expected = pmDtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss a z").parse("Sat, 08 Sep 2018 16:08:09 pm UTC"))
    }

    @Test
    fun testParsingDateTimesWithDeciSeconds() {
        var dtmilli = 1536379689009L
        assertEquals(dtmilli, DateTime(2018, 9, 8, 4, 8, 9, 9).unix)
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.9 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.S z").parse("Sat, 08 Sep 2018 04:08:09.9 UTC"))
    }

    @Test
    fun testParsingDateTimesWithCentiSeconds() {
        var dtmilli = 1536379689099L
        assertEquals(dtmilli, DateTime(2018, 9, 8, 4, 8, 9, 99).unix)
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.99 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.SS z").parse("Sat, 08 Sep 2018 04:08:09.99 UTC"))
    }

    @Test
    fun testParsingDateTimesWithMilliseconds() {
        var dtmilli = 1536379689999L
        assertEquals(dtmilli, DateTime(2018, 9, 8, 4, 8, 9, 999).unix)
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.999 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSS z").parse("Sat, 08 Sep 2018 04:08:09.999 UTC"))
    }

    @Test
    fun testParsingDateTimesWithGreaterPrecisionThanMillisecond() {
        val dtmilli = 1536379689999L
        assertEquals(dtmilli, DateTime(2018, 9, 8, 4, 8, 9, 999).unix)
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.9999 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSSS z").parse("Sat, 08 Sep 2018 04:08:09.9999 UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.99999 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSSSS z").parse("Sat, 08 Sep 2018 04:08:09.99999 UTC"))
        assertEquals(message = "Sat, 08 Sep 2018 04:08:09.999999 UTC",
                expected = dtmilli,
                actual = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss.SSSSSS z").parse("Sat, 08 Sep 2018 04:08:09.999999 UTC"))
    }


	@Test
	fun testParse() {
		assertEquals("Mon, 18 Sep 2017 04:58:45 UTC", HttpDate.format(1505710725916L))
	}

	@kotlin.test.Test
	fun testReverseParse() {
		val STR = "Tue, 19 Sep 2017 00:58:45 UTC"
		assertEquals(STR, HttpDate.format(HttpDate.parse(STR)))
	}

	@kotlin.test.Test
	fun testCheckedCreation() {
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime(2017, 9, 18, 23, 58, 45)))
	}

	@kotlin.test.Test
	fun testCreatedAdjusted() {
		assertEquals(
			"Thu, 18 Jan 2018 23:58:45 UTC",
			HttpDate.format(DateTime.createAdjusted(2017, 13, 18, 23, 58, 45))
		)
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime.createAdjusted(2017, 9, 18, 23, 58, 45)))
		assertEquals(
			"Mon, 01 Jan 2018 00:00:01 UTC",
			HttpDate.format(DateTime.createAdjusted(2017, 12, 31, 23, 59, 61))
		)
		assertEquals(
			"Thu, 21 Mar 2024 19:32:20 UTC",
			HttpDate.format(DateTime.createAdjusted(2017, 12, 31, 23, 59, 200_000_000))
		)
	}

	@kotlin.test.Test
	fun testCreatedClamped() {
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime.createClamped(2017, 9, 18, 23, 58, 45)))
		assertEquals("Mon, 18 Dec 2017 23:58:45 UTC", HttpDate.format(DateTime.createClamped(2017, 13, 18, 23, 58, 45)))
	}
}
