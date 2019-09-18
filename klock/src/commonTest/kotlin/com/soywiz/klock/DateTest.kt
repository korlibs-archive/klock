package com.soywiz.klock

import kotlin.test.Test
import kotlin.test.assertEquals

class DateTest {
	@Test
	fun test() {
		val date = Date(2019, Month.September, 18)
		val time = Time(13, 9, 37, 150)
		assertEquals("2019-09-18", date.toString())
		assertEquals("13:09:37.150", time.toString())
		assertEquals("Wed, 18 Sep 2019 00:00:00 UTC", (date.dateTimeDayStart).toString())
		assertEquals("Wed, 18 Sep 2019 13:09:37 UTC", (date + time).toString())
	}
}