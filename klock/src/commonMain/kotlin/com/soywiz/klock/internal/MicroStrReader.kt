package com.soywiz.klock.internal

internal class MicroStrReader(val str: String, var offset: Int = 0) {
    val length get() = str.length
    val available get() = str.length - offset
    val hasMore get() = offset < str.length
    fun readChar(): Char = str[offset++]
    fun tryRead(str: String): Boolean {
        if (str.length > available) return false
        for (n in 0 until str.length) if (this.str[offset + n] != str[n]) return false
        offset += str.length
        return true
    }
}