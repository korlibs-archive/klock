package com.soywiz.klock

import kotlin.test.Test
import kotlin.test.assertEquals

class TimeTest {
	@Test
	fun test() {
		assertEquals("09:30:00.000", (Time(9) + 30.minutes).toString())
	}
}