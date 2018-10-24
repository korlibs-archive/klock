package com.soywiz.klock

/**
 * Represents a Year in a typed way.
 *
 * A year is a set of 365 days or 366 for leap years.
 * It is the time it takes the earth to fully orbit the sun.
 *
 * The integrated model is capable of determine if a year is leap for years 1 until 9999 inclusive.
 */
inline class Year(val year: Int) : Comparable<Year> {
    companion object {
        /**
         * Creates a Year instance checking that the year is between 1 and 9999 inclusive.
         *
         * It throws a [DateException] is the year is not in the 1..9999 range.
         */
        fun checked(year: Int) = year.apply { if (year !in 1..9999) throw DateException("Year $year not in 1..9999") }

        /**
         * Determines if a year is leap checking that the year is between 1..9999 or throwing a [DateException] when outside that range.
         */
        fun isLeapChecked(year: Int): Boolean = isLeap(checked(year))

        /**
         * Determines if a year is leap. The model works for years between 1..9999, outside this range, the result might be invalid.
         */
        fun isLeap(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    /**
     * Determines if this year is leap checking that the year is between 1..9999 or throwing a [DateException] when outside that range.
     */
    val isLeapChecked get() = Year.isLeapChecked(year)

    /**
     * Determines if this year is leap. The model works for years between 1..9999, outside this range, the result might be invalid.
     */
    val isLeap get() = Year.isLeap(year)

    /**
     * Compares two years.
     */
    override fun compareTo(other: Year): Int = this.year.compareTo(other.year)
}
