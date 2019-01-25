package com.soywiz.klock

import kotlin.math.*

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
        fun isLeap(year: Int): Boolean = (year % 4 == 0) && (year % 100 != 0 || year % 400 == 0)

        /**
         * Computes the year from the number of days since 0001-01-01.
         */
        fun fromDays(days: Int): Year {
            // https://en.wikipedia.org/wiki/Leap_year#Algorithm
            // Each 400 years the modular cycle is completed

            val v400 = days / DAYS_PER_400_YEARS
            val r400 = days % DAYS_PER_400_YEARS

            val v100 = min(r400 / DAYS_PER_100_YEARS, 3)
            val r100 = r400 % DAYS_PER_100_YEARS

            val v4 = r100 / DAYS_PER_4_YEARS
            val r4 = r100 % DAYS_PER_4_YEARS

            val v1 = min(r4 / DAYS_COMMON, 3)

            return Year(1 + v1 + (v4 * 4) + (v100 * 100) + (v400 * 400))
        }

        /**
         * Get the number of days of a year depending on being leap or not.
         * Normal, non leap years contains 365 days, while leap ones 366.
         */
        fun days(isLeap: Boolean) = if (isLeap) DAYS_LEAP else DAYS_COMMON

        /**
         * Return the number of leap years that happened between 1 and the specified [year].
         */
        fun leapCountSinceOne(year: Int): Int = ((year - 1) / 4) - ((year - 1) / 100) + ((year - 1) / 400)

        /**
         * Number of days since 1 and the beginning of the specified [year].
         */
        fun daysSinceOne(year: Int): Int = DAYS_COMMON * (year - 1) + leapCountSinceOne(year)

        /**
         * Number of days in a normal year.
         */
        const val DAYS_COMMON = 365

        /**
         * Number of days in a leap year.
         */
        const val DAYS_LEAP = 366

        private const val LEAP_PER_4_YEARS = 1
        private const val LEAP_PER_100_YEARS = 24 // 24 or 25 (25 the first chunk)
        private const val LEAP_PER_400_YEARS = 97

        private const val DAYS_PER_4_YEARS = 4 * DAYS_COMMON + LEAP_PER_4_YEARS
        private const val DAYS_PER_100_YEARS = 100 * DAYS_COMMON + LEAP_PER_100_YEARS
        private const val DAYS_PER_400_YEARS = 400 * DAYS_COMMON + LEAP_PER_400_YEARS
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
     * Total days of this year, 365 (non leap) [DAYS_COMMON] or 366 (leap) [DAYS_LEAP].
     */
    val days: Int get() = Year.days(isLeap)

    /**
     * Number of leap years since the year 1 (without including this one)
     */
    val leapCountSinceOne: Int get() = leapCountSinceOne(year)

    /**
     * Number of days since year 1 to reach this year
     */
    val daysSinceOne: Int get() = daysSinceOne(year)

    /**
     * Compares two years.
     */
    override fun compareTo(other: Year): Int = this.year.compareTo(other.year)

    operator fun plus(delta: Int): Year = Year(year + delta)
    operator fun minus(delta: Int): Year = Year(year - delta)
    operator fun minus(other: Year): Int = this.year - other.year
}
