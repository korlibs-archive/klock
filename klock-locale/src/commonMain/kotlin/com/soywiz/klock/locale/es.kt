package com.soywiz.klock.locale

import com.soywiz.klock.KlockLocale

object SpanishKlockLocale : KlockLocale() {
	override val ISO639_1 = "es"

	override val daysOfWeek = listOf(
		"domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"
	)
	override val months = listOf(
		"enero", "febrero", "marzo", "abril", "mayo", "junio",
		"julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
	)
}
