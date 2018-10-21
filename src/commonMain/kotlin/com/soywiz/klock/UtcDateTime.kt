package com.soywiz.klock

import com.soywiz.klock.internal.*

/**
 * Represents a Date in UTC (GMT+00) with millisecond precision.
 *
 * It is internally represented as an inlined double, thus doesn't allocate in any target including JS.
 * It can represent without loss dates between (-(2 ** 52) and (2 ** 52)):
 * - Thu Aug 10 -140744 07:15:45 GMT-0014 (Central European Summer Time)
 * - Wed May 23 144683 18:29:30 GMT+0200 (Central European Summer Time)
 */
inline class UtcDateTime(val unixMillis: Double) : DateTime, Comparable<UtcDateTime> {
    val internalMillis get() = EPOCH_INTERNAL_MILLIS + unixMillis

    companion object {
        internal const val EPOCH_INTERNAL_MILLIS = 62135596800000.0 // Millis since 00-00-0000 00:00 UTC to UNIX EPOCH

        private const val DATE_PART_YEAR = 0
        private const val DATE_PART_DAY_OF_YEAR = 1
        private const val DATE_PART_MONTH = 2
        private const val DATE_PART_DAY = 3

        /**
         * Returns milliseconds since EPOCH.
         */
        internal fun dateToMillisUnchecked(year: Int, month: Int, day: Int): Double {
            val y = (year - 1)
            val n = y * 365 + y / 4 - y / 100 + y / 400 + Month.daysToStart(month, year) + day - 1
            return n.toDouble() * MILLIS_PER_DAY.toDouble() - EPOCH_INTERNAL_MILLIS
        }

        internal fun timeToMillisUnchecked(hour: Int, minute: Int, second: Int): Double {
            return (hour.toDouble() * 3600 + minute.toDouble() * 60 + second.toDouble()) * MILLIS_PER_SECOND
        }

        internal fun dateToMillis(year: Int, month: Int, day: Int): Double {
            //Year.checked(year)
            Month.check(month)
            if (day !in 1..Month.days(month, year)) {
                throw DateException("Day $day not valid for year=$year and month=$month")
            }
            return dateToMillisUnchecked(year, month, day)
        }

        internal fun timeToMillis(hour: Int, minute: Int, second: Int): Double {
            if (hour !in 0..23) throw DateException("Hour $hour not in 0..23")
            if (minute !in 0..59) throw DateException("Minute $minute not in 0..59")
            if (second !in 0..59) throw DateException("Second $second not in 0..59")
            return timeToMillisUnchecked(hour, minute, second)
        }

        /**
         * [millis] are 00-00-0000 based.
         */
        internal fun getDatePart(millis: Double, part: Int): Int {
            var n = (millis / MILLIS_PER_DAY).toInt()
            val y400 = n / DAYS_PER_400_YEARS
            n -= y400 * DAYS_PER_400_YEARS
            var y100 = n / DAYS_PER_100_YEARS
            if (y100 == 4) y100 = 3
            n -= y100 * DAYS_PER_100_YEARS
            val y4 = n / DAYS_PER_4_YEARS
            n -= y4 * DAYS_PER_4_YEARS
            var y1 = n / DAYS_PER_YEAR
            if (y1 == 4) y1 = 3
            if (part == DATE_PART_YEAR) return y400 * 400 + y100 * 100 + y4 * 4 + y1 + 1
            n -= y1 * DAYS_PER_YEAR
            if (part == DATE_PART_DAY_OF_YEAR) return n + 1
            val leapYear = y1 == 3 && (y4 != 24 || y100 == 3)
            var m = n shr 5 + 1
            while (n >= Month.daysToEnd(m, leapYear)) m++
            return if (part == DATE_PART_MONTH) m else n - Month.daysToStart(m, leapYear) + 1
        }
    }

    private fun getDatePart(part: Int): Int = Companion.getDatePart(internalMillis, part)

    override val offset: Int get() = 0
    override val utc: UtcDateTime get() = this
    override val unixDouble: Double get() = unixMillis
    override val year: Int get() = getDatePart(DATE_PART_YEAR)
    override val month1: Int get() = getDatePart(DATE_PART_MONTH)
    override val dayOfMonth: Int get() = getDatePart(DATE_PART_DAY)
    override val dayOfWeekInt: Int get() = ((internalMillis / MILLIS_PER_DAY + 1) % 7).toInt()
    override val dayOfYear: Int get() = getDatePart(DATE_PART_DAY_OF_YEAR)
    override val hours: Int get() = (((internalMillis / MILLIS_PER_HOUR) % 24).toInt())
    override val minutes: Int get() = ((internalMillis / MILLIS_PER_MINUTE) % 60).toInt()
    override val seconds: Int get() = ((internalMillis / MILLIS_PER_SECOND) % 60).toInt()
    override val milliseconds: Int get() = ((internalMillis) % 1000).toInt()
    override val timeZone: String get() = "UTC"

    override fun add(deltaMonths: Int, deltaMilliseconds: Long): DateTime = when {
        deltaMonths == 0 && deltaMilliseconds == 0L -> this
        deltaMonths == 0 -> UtcDateTime(this.unixMillis + deltaMilliseconds)
        else -> {
            var year = this.year
            var month = this.month.index
            var day = this.dayOfMonth
            val i = month - 1 + deltaMonths

            if (i >= 0) {
                month = i % 12 + 1
                year += i / 12
            } else {
                month = 12 + (i + 1) % 12
                year += (i - 11) / 12
            }
            //Year.checked(year)
            val days = Month.days(month, year)
            if (day > days) day = days

            UtcDateTime(dateToMillisUnchecked(year, month, day) + (internalMillis % MILLIS_PER_DAY) + deltaMilliseconds)
        }
    }

    override operator fun compareTo(other: UtcDateTime): Int = this.unixDouble.compareTo(other.unixDouble)
    //override fun hashCode(): Int = internalMillis.hashCode()
    //override fun equals(other: Any?): Boolean = this.unix == (other as? DateTime?)?.unix
    override fun toString(): String = SimplerDateFormat.DEFAULT_FORMAT.format(this)
}
