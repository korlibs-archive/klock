package com.soywiz.klock.locale

import com.soywiz.klock.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KlockLocaleTest {
	val date = DateTime(year = 2019, month = Month.March, day = 13, hour = 21, minute = 36, second = 45, milliseconds = 512)

	@Test
	fun testSpanishLocale() {
		assertEquals(
			"Mié, 13 Mar 2019 21:36:45 UTC",
			date.toString(DateFormat.DEFAULT_FORMAT.withLocale(KlockLocale.spanish))
		)
		assertEquals("13/03/2019 21:36:45", SpanishKlockLocale.formatDateTimeMedium.format(date))
		assertEquals("13/03/19 21:36", SpanishKlockLocale.formatDateTimeShort.format(date))
		assertEquals("Miércoles, 13 de Marzo de 2019", SpanishKlockLocale.formatDateFull.format(date))
		assertEquals("13 de Marzo de 2019", SpanishKlockLocale.formatDateLong.format(date))
		assertEquals("13/03/2019", SpanishKlockLocale.formatDateMedium.format(date))
		assertEquals("13/03/19", SpanishKlockLocale.formatDateShort.format(date))
		assertEquals("21:36:45", SpanishKlockLocale.formatTimeMedium.format(date))
		assertEquals("21:36", SpanishKlockLocale.formatTimeShort.format(date))
	}

	@Test
	fun testTemporalSetDefault() {
		assertEquals("Wed, 13 Mar 2019 21:36:45 UTC", date.toString())
		KlockLocale.setTemporarily(KlockLocale.spanish) {
			assertEquals("Mié, 13 Mar 2019 21:36:45 UTC", date.toString())
		}
		assertEquals("Wed, 13 Mar 2019 21:36:45 UTC", date.toString())
	}
}