package com.klock

import com.soywiz.klock.Year
import kotlin.test.*

class YearTest {
	@Test
	fun testLeap() {
		assertEquals(true, Year.isLeap(2000))
		assertEquals(false, Year.isLeap(2006))
	}
}
