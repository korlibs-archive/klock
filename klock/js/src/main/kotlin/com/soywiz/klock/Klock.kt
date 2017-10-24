package com.soywiz.klock

actual object Klock {
	actual fun currentTimeMillis(): Long = js("Date.now()").unsafeCast<Double>().toLong()

	actual fun getLocalTimezoneOffset(unix: Long): Int {
		@Suppress("UNUSED_VARIABLE")
		val rtime = unix.toDouble()
		return js("-(new Date(rtime)).getTimezoneOffset()").unsafeCast<Int>()
	}
}