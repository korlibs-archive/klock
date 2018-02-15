package com.klock

import com.soywiz.klock.DateTime
import com.soywiz.klock.months
import kotlin.test.*

class TimeDistanceTest {
	@Test
	fun testAddDistance() {
		assertEquals(DateTime(2018, 1, 13), DateTime(2017, 12, 13) + 1.months)
	}
}
