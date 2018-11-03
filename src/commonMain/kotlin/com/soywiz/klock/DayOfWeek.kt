package com.soywiz.klock

/**
 * Represents the day of the week.
 *
 * [Sunday], [Monday], [Tuesday], [Wednesday], [Thursday], [Friday], [Saturday].
 */
enum class DayOfWeek(
    /** 0: [Sunday], 1: [Monday], 2: [Tuesday], 3: [Wednesday], 4: [Thursday], 5: [Friday], 6: [Saturday] */
    val index0: Int
) {
    Sunday(0),
    Monday(1),
    Tuesday(2),
    Wednesday(3),
    Thursday(4),
    Friday(5),
    Saturday(6);

    /**
     * 1: [Sunday], 2: [Monday], 3: [Tuesday], 4: [Wednesday], 5: [Thursday], 6: [Friday], 7: [Saturday]
     */
    val index1 get() = index0 + 1

    companion object {
        private val BY_INDEX0 = values()

        /**
         * 0: [Sunday], 1: [Monday], 2: [Tuesday], 3: [Wednesday], 4: [Thursday], 5: [Friday], 6: [Saturday]
         */
        operator fun get(index0: Int) = BY_INDEX0[index0]
    }
}
