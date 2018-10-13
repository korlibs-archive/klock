package com.soywiz.klock

import kotlin.test.*

class OffsetDateTimeTest {
	@Test
	fun name() {
		val date1 = (DateTime(2018, 3, 2, 1) + 100.seconds)
		val date2 = (DateTime(2018, 3, 2, 1) + 100.seconds + 60.minutes)
		assertEquals(date1.toOffset(+60), date1.toOffset(+60))
		assertEquals(date1.toOffset(+60), date2.toOffset(+0))
		assertEquals(date1.toOffset(+60), date2)
		assertNotEquals(date1, date2)
		assertTrue(date1.toOffset(0) < date1.toOffset(+60))
		assertTrue(date1.toOffset(0) == date1.toOffset(+0))
		assertTrue(date1.toOffset(+60) > date1.toOffset(+0))
		assertTrue(date1.toOffset(+60) > date1)
	}
}
