package com.soywiz.klock

enum class DayOfWeek(val index: Int) {
	Sunday(0), Monday(1), Tuesday(2), Wednesday(3), Thursday(4), Friday(5), Saturday(6);

	companion object {
		private val BY_INDEX = values()
		operator fun get(index: Int) = BY_INDEX[index]
	}
}
