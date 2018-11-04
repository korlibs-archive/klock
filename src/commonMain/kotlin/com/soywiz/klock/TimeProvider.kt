package com.soywiz.klock

/** Class to provide time that can be overriden to mock or change its behaviour. */
open class TimeProvider {
    /** Returns a [DateTime] for this provider. */
    open fun now(): DateTime = DateTime.now()

    companion object {
        /** Constructs a [TimeProvider] from a [callback] producing a [DateTime]. */
        operator fun invoke(callback: () -> DateTime) = object : TimeProvider() {
            override fun now(): DateTime = callback()
        }
    }
}
