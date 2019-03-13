package com.soywiz.klock.locale

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import com.soywiz.klock.KlockLocale
import com.soywiz.klock.Month
import kotlin.test.Test
import kotlin.test.assertEquals

class KlockLocaleTest {
	val date = DateTime(2019, Month.March, 13)

	@Test
	fun testSpanishLocale() {
		assertEquals(
			"Mié, 13 Mar 2019 00:00:00 UTC",
			date.toString(DateFormat.DEFAULT_FORMAT.withLocale(SpanishKlockLocale))
		)
	}

	@Test
	fun testTemporalSetDefault() {
		assertEquals("Wed, 13 Mar 2019 00:00:00 UTC", date.toString())
		KlockLocale.temporalSet(SpanishKlockLocale) {
			assertEquals("Mié, 13 Mar 2019 00:00:00 UTC", date.toString())
		}
		assertEquals("Wed, 13 Mar 2019 00:00:00 UTC", date.toString())
	}
}