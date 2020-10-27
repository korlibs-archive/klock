package com.soywiz.klock

import com.soywiz.klock.locale.*
import org.junit.Ignore
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.test.assertEquals

class KlockLocaleTestJvm {

    private val outputPattern = "EEEE, d MMMM yyyy HH:mm:ss"

    private val klockDate = DateTime(
        year = 1995,
        month = Month.January,
        day = 18,
        hour = 21,
        minute = 36,
        second = 45
    )
    private val javaDate = LocalDateTime.of(
        1995, // year
        java.time.Month.JANUARY, // month
        18, // day of month
        21, // hour
        36, // minute
        45 // second
    )


    @Test
    fun assertDatesAreTheSame() {
        val klockLong = klockDate.unixMillisLong
        val javaLong = javaDate.toInstant(ZoneOffset.UTC).toEpochMilli()
        assertEquals(javaLong, klockLong)
    }

    private fun LocalDateTime.format(pattern: String, locale: Locale) = this.format(
        DateTimeFormatter.ofPattern(pattern, locale)
    )

    @Test
    fun assertEnglishLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.ENGLISH)

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.english) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertNorwegianLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("no-nb"))

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.norwegian) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertSpanishLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("es"))

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.spanish) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertFrenchLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("fr"))

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.french) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertGermanLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.GERMAN)

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.german) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertJapaneseLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.JAPAN)

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.japanese) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertDutchLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("nl"))

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.dutch) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertPortugueseLocalization() {
        // Java DateTime formats wrong in java 8. Hardcoding expected value instead.
        // val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("pt"))
        val expectedOutput = "quarta-feira, 18 janeiro 1995 21:36:45"

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.portuguese) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(expectedOutput, klockOutput)
    }

    @Test
    fun assertRussianLocalization() {
        // Java DateTime formats wrong in java 8. Hardcoding expected value instead.
        // val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("ru"))
        val expectedOutput = "среда, 18 января 1995 21:36:45"

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.russian) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(expectedOutput, klockOutput)
    }

    @Test
    fun assertKoreanLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.KOREA)

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.korean) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertChineseLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.CHINA)

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.chinese) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

    @Test
    fun assertUkrainianLocalization() {
        val javaOutput = javaDate.format(outputPattern, Locale.forLanguageTag("uk"))

        val klockOutput = KlockLocale.setTemporarily(KlockLocale.ukrainian) {
            DateFormat(outputPattern).format(klockDate)
        }

        assertEquals(javaOutput, klockOutput)
    }

}
