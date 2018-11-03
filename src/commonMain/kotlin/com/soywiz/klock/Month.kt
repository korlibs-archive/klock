package com.soywiz.klock

import com.soywiz.klock.internal.*
import kotlin.math.*

/**
 * Represents one the twelve months of the year.
 */
enum class Month(
    /** 1: [January], 2: [February], 3: [March], 4: [April], 5: [May], 6: [June], 7: [July], 8: [August], 9: [September], 10: [October], 11: [November], 12: [December] */
    val index1: Int
) {
    January(1), // 31
    February(2), // 28/29
    March(3), // 31
    April(4), // 30
    May(5), // 31
    June(6), // 30
    July(7), // 31
    August(8), // 31
    September(9), // 30
    October(10), // 31
    November(11), // 30
    December(12); // 31

    val index0: Int get() = index1 - 1

    fun days(isLeap: Boolean): Int = DAYS_TO_MONTH(isLeap).let { days -> days[index1] - days[index0] }
    fun daysToStart(isLeap: Boolean): Int = DAYS_TO_MONTH(isLeap)[index0]
    fun daysToEnd(isLeap: Boolean): Int = DAYS_TO_MONTH(isLeap)[index1]

    fun days(year: Int): Int = days(Year(year).isLeap)
    fun daysToStart(year: Int): Int = daysToStart(Year(year).isLeap)
    fun daysToEnd(year: Int): Int = daysToEnd(Year(year).isLeap)

    fun days(year: Year): Int = days(year.isLeap)
    fun daysToStart(year: Year): Int = daysToStart(year.isLeap)
    fun daysToEnd(year: Year): Int = daysToEnd(year.isLeap)

    operator fun plus(months: Int): Month = Month[index1 + months]
    operator fun minus(months: Int): Month = Month[index1 - months]

    operator fun minus(other: Month): Int = abs(this.index0 - other.index0)

    companion object {
        /**
         * Number of months in a year (12).
         */
        const val Count = 12

        operator fun invoke(index1: Int) = adjusted(index1)
        operator fun get(index1: Int) = adjusted(index1)

        fun adjusted(index1: Int) = BY_INDEX0[(index1 - 1) umod 12]
        fun checked(index1: Int) = BY_INDEX0[index1.also { if (index1 !in 1..12) throw DateException("Month $index1 not in 1..12") } - 1]

        fun fromDayOfYear(day: Int, isLeap: Boolean): Month? {
            val days = DAYS_TO_MONTH(isLeap)
            val day0 = day - 1
            val guess = day0 / 32

            if (guess in 0..11 && day0 in days[guess] until days[guess + 1]) return Month[guess + 1]
            if (guess in 0..10 && day0 in days[guess + 1] until days[guess + 2]) return Month[guess + 2]

            return null
        }

        private val BY_INDEX0 = values()
        private fun DAYS_TO_MONTH(isLeap: Boolean): IntArray = if (isLeap) DAYS_TO_MONTH_366 else DAYS_TO_MONTH_365
        private val DAYS_TO_MONTH_366 = intArrayOf(0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366)
        private val DAYS_TO_MONTH_365 = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365)
    }
}
