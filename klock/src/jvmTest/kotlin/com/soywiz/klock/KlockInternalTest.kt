package com.soywiz.klock

import com.soywiz.klock.internal.KlockInternalJvm
import com.soywiz.klock.internal.TemporalKlockInternalJvm
import kotlin.test.Test
import kotlin.test.assertEquals

class KlockInternalTest {
    @Test
    fun testThatNowLocalHasTimezoneIntoAccount() {
        TemporalKlockInternalJvm(object : KlockInternalJvm {
            override val currentTime: Double get() = 1561403754469.0
            override val microClock: Double get() = TODO()
            override fun localTimezoneOffsetMinutes(time: DateTime): TimeSpan = 2.hours
        }) {
            assertEquals("Mon, 24 Jun 2019 19:15:54 UTC", DateTime.now().toString())
            assertEquals("Mon, 24 Jun 2019 21:15:54 GMT+0200", DateTime.nowLocal().toString())
        }
    }
}