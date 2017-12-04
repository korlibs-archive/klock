package com.klock

import com.soywiz.klock.DateTime
import org.junit.Test
import kotlin.test.assertEquals

class DateTimeTest {
	@Test
	fun name() {
		assertEquals("Mon, 04 Dec 2017 04:35:37 UTC", DateTime.fromString("2017-12-04T04:35:37Z").toString())
	}
}