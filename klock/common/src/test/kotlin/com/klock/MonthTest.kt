package com.klock

import com.soywiz.klock.Month
import org.junit.Test
import kotlin.test.assertEquals

class MonthTest {
	@Test
	fun testBasicMonthMetrics() {
		assertEquals(
			listOf(
				0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334,
				0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335
			),
			listOf(false, true).map { leap -> (1..12).map { Month.daysToStart(it, isLeap = leap) } }.flatMap { it }
		)

		assertEquals(
			listOf(
				0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365,
				0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366
			),
			listOf(false, true).map { leap -> (0..12).map { Month.daysToEnd(it, isLeap = leap) } }.flatMap { it }
		)

		assertEquals(
			listOf(
				31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31,
				31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
			),
			listOf(false, true).map { leap -> (1..12).map { Month.days(it, isLeap = leap) } }.flatMap { it }
		)
	}
}