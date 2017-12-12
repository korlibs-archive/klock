package com.klock

import com.soywiz.klock.DateTime
import com.soywiz.klock.months
import org.junit.Test
import kotlin.test.assertEquals

class TimeDistanceTest {
	@Test
	fun testAddDistance() {
		assertEquals(DateTime(2018, 1, 13), DateTime(2017, 12, 13) + 1.months)
	}
}