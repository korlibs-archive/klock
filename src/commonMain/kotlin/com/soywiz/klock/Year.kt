package com.soywiz.klock

inline class Year(val year: Int) : Comparable<Year> {
	companion object {
		fun checked(year: Int) = year.apply { if (year !in 1..9999) throw DateException("Year $year not in 1..9999") }
		fun isLeapChecked(year: Int): Boolean = isLeap(checked(year))
		fun isLeap(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
	}

	override fun compareTo(other: Year): Int = this.year.compareTo(other.year)
}
