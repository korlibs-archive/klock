package com.soywiz.klock.locale

import com.soywiz.klock.DayOfWeek
import com.soywiz.klock.KlockLocale

val KlockLocale.Companion.spanish get() = SpanishKlockLocale

open class SpanishKlockLocale : KlockLocale() {
	companion object : SpanishKlockLocale()

	override val ISO639_1 = "es"

	override val h12Marker = listOf("a.m.", "p.m.")

	override val firstDayOfWeek: DayOfWeek = DayOfWeek.Monday

	override val daysOfWeek = listOf(
		"domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"
	)
	override val months = listOf(
		"enero", "febrero", "marzo", "abril", "mayo", "junio",
		"julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
	)

	override val formatDateTimeMediumStr = "dd/MM/yyyy HH:mm:ss"
	override val formatDateTimeShortStr = "dd/MM/yy HH:mm"

	override val formatDateFullStr = "EEEE, d 'de' MMMM 'de' y"
	override val formatDateLongStr = "d 'de' MMMM 'de' y"
	override val formatDateMediumStr = "dd/MM/yyyy"
	override val formatDateShortStr = "dd/MM/yy"

	override val formatTimeMediumStr = "HH:mm:ss"
	override val formatTimeShortStr = "HH:mm"
}
