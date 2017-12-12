package com.soywiz.klock

actual object Klock {
	actual val VERSION: String = KlockExt.VERSION

	actual fun currentTimeMillis(): Long = js("Date.now()").unsafeCast<Double>().toLong()
	actual fun currentTimeMillisDouble(): Double = js("Date.now()").unsafeCast<Double>()

	actual fun getLocalTimezoneOffset(unix: Long): Int {
		@Suppress("UNUSED_VARIABLE")
		val rtime = unix.toDouble()
		return js("-(new Date(rtime)).getTimezoneOffset()").unsafeCast<Int>()
	}
}