package com.klock

import com.soywiz.klock.Year
import org.junit.Test
import kotlin.test.assertEquals

class YearTest {
	@Test
	fun testLeap() {
		assertEquals(true, Year.isLeap(2000))
		assertEquals(false, Year.isLeap(2006))
	}
}