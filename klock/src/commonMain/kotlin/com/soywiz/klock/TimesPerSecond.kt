package com.soywiz.klock

val TimeSpan.hz get() = TimesPerSecond(1.0 / this.seconds)
val Int.hz get() = TimesPerSecond(this.toDouble())
val Double.hz get() = TimesPerSecond(this)

val TimeSpan.timesPerSecond get() = TimesPerSecond(1.0 / this.seconds)
val Int.timesPerSecond get() = TimesPerSecond(this.toDouble())
val Double.timesPerSecond get() = TimesPerSecond(this)

inline class TimesPerSecond(val hertz: Double) {
    val timeSpan get() = (1.0 / this.hertz).seconds
}
