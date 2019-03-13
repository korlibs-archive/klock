package com.soywiz.klock

import com.soywiz.klock.internal.substr
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
private var KlockLocale_default: KlockLocale = KlockLocale.English

abstract class KlockLocale {
	abstract val ISO639_1: String
	abstract val daysOfWeek: List<String>
	abstract val months: List<String>
	abstract val firstDayOfWeek: DayOfWeek
	open val months3 by lazy { months.map { it.substr(0, 3) } }
	open val h12Marker = listOf("AM", "OM")

	protected open val formatDateTimeMediumStr = "MMM d, y h:mm:ss a"
	protected open val formatDateTimeShortStr = "M/d/yy h:mm a"

	protected open val formatDateFullStr = "EEEE, MMMM d, y"
	protected open val formatDateLongStr = "MMMM d, y"
	protected open val formatDateMediumStr = "MMM d, y"
	protected open val formatDateShortStr = "M/d/yy"

	protected open val formatTimeMediumStr = "h:mm:ss a"
	protected open val formatTimeShortStr = "h:mm a"

	open fun isWeekend(dayOfWeek: DayOfWeek): Boolean = dayOfWeek == DayOfWeek.Saturday || dayOfWeek == DayOfWeek.Sunday

	val formatDateFull by lazy { PatternDateFormat(formatDateFullStr, this) }
	val formatDateLong by lazy { PatternDateFormat(formatDateLongStr, this) }
	val formatDateTimeMedium by lazy { PatternDateFormat(formatDateTimeMediumStr, this) }
	val formatDateMedium by lazy { PatternDateFormat(formatDateMediumStr, this) }
	val formatTimeMedium by lazy { PatternDateFormat(formatTimeMediumStr, this) }
	val formatDateTimeShort by lazy { PatternDateFormat(formatDateTimeShortStr, this) }
	val formatDateShort by lazy { PatternDateFormat(formatDateShortStr, this) }
	val formatTimeShort by lazy { PatternDateFormat(formatTimeShortStr, this) }

	companion object {
		val english get() = English

		var default: KlockLocale
			set(value) = run { KlockLocale_default = value }
			get() = KlockLocale_default

		inline fun <R> setTemporarily(locale: KlockLocale, callback: () -> R): R {
			val old = default
			default = locale
			try {
				return callback()
			} finally {
				default = old
			}
		}
	}

	open class English : KlockLocale() {
		companion object : English()

		override val ISO639_1 = "en"

		override val firstDayOfWeek: DayOfWeek = DayOfWeek.Sunday

		override val daysOfWeek: List<String> = listOf(
			"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"
		)
		override val months: List<String> = listOf(
			"january", "february", "march", "april", "may", "june",
			"july", "august", "september", "october", "november", "december"
		)
	}
}
