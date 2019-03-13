package com.soywiz.klock

import com.soywiz.klock.internal.substr
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
private var KlockLocale_default: KlockLocale = EnglishKlockLocale

abstract class KlockLocale {
	abstract val ISO639_1: String
	abstract val daysOfWeek: List<String>
	abstract val months: List<String>
	open val months3 by lazy { months.map { it.substr(0, 3) } }

	companion object {
		var default: KlockLocale
			set(value) = run { KlockLocale_default = value }
			get() = KlockLocale_default

		inline fun <R> temporalSet(locale: KlockLocale, callback: () -> R): R {
			val old = default
			default = locale
			try {
				return callback()
			} finally {
				default = old
			}
		}
	}
}

object EnglishKlockLocale : KlockLocale() {
	override val ISO639_1 = "en"

	override val daysOfWeek: List<String> = listOf(
		"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
	)
	override val months: List<String> = listOf(
		"january", "february", "march", "april", "may", "june",
		"july", "august", "september", "october", "november", "december"
	)
}
