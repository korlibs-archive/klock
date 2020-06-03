package com.soywiz.klock

val infiniteTimes get() = Times.INFINITE
inline val Int.times get() = Times(this)

inline class Times(val count: Int) {
    companion object {
        val ZERO = Times(0)
        val ONE = Times(1)
        val INFINITE = Times(Int.MIN_VALUE)
    }
    val isInfinite get() = this == INFINITE
    val isFinite get() = !isInfinite
    val hasMore get() = this != ZERO
    val oneLess get() = if (this == INFINITE) INFINITE else Times(count - 1)
    operator fun plus(other: Times) = if (this == INFINITE || other == INFINITE) INFINITE else Times(this.count + other.count)
    operator fun minus(other: Times) = when {
        this == other -> ZERO
        this == INFINITE || other == INFINITE -> INFINITE
        else -> Times(this.count - other.count)
    }
    operator fun times(other: Int) = if (this == INFINITE) INFINITE else Times(this.count * other)
    operator fun div(other: Int) = if (this == INFINITE) INFINITE else Times(this.count / other)
    override fun toString(): String = if (this == INFINITE) "$count times" else "Infinite times"
}
