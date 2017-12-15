package com.klock

import com.soywiz.klock.DateTime
import com.soywiz.klock.SimplerDateFormat
import org.junit.Test
import kotlin.test.assertEquals

class DateTimeTest {
	val HttpDate = SimplerDateFormat("EEE, dd MMM yyyy HH:mm:ss z")

	@Test
	fun testFromString() {
		assertEquals("Mon, 04 Dec 2017 04:35:37 UTC", DateTime.fromString("2017-12-04T04:35:37Z").toString())
	}

	@Test
	fun testParse() {
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(1505779125916L))
	}

	@Test
	fun testReverseParse() {
		val STR = "Tue, 19 Sep 2017 00:58:45 UTC"
		assertEquals(STR, HttpDate.format(HttpDate.parse(STR)))
	}

	@Test
	fun testCheckedCreation() {
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime(2017, 9, 18, 23, 58, 45)))
	}

	@Test
	fun testCreatedAdjusted() {
		assertEquals("Thu, 18 Jan 2018 23:58:45 UTC", HttpDate.format(DateTime.createAdjusted(2017, 13, 18, 23, 58, 45)))
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime.createAdjusted(2017, 9, 18, 23, 58, 45)))
		assertEquals("Mon, 01 Jan 2018 00:00:01 UTC", HttpDate.format(DateTime.createAdjusted(2017, 12, 31, 23, 59, 61)))
		assertEquals("Thu, 21 Mar 2024 19:32:20 UTC", HttpDate.format(DateTime.createAdjusted(2017, 12, 31, 23, 59, 200_000_000)))
	}

	@Test
	fun testCreatedClamped() {
		assertEquals("Mon, 18 Sep 2017 23:58:45 UTC", HttpDate.format(DateTime.createClamped(2017, 9, 18, 23, 58, 45)))
		assertEquals("Mon, 18 Dec 2017 23:58:45 UTC", HttpDate.format(DateTime.createClamped(2017, 13, 18, 23, 58, 45)))
	}
}
