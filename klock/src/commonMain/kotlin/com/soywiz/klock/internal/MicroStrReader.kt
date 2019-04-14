package com.soywiz.klock.internal

import kotlin.math.pow

internal class MicroStrReader(val str: String, var offset: Int = 0) {
    val length get() = str.length
    val available get() = str.length - offset
    val hasMore get() = offset < str.length
    fun peekChar(): Char = str[offset]
    fun readChar(): Char = str[offset++]
    fun tryRead(str: String): Boolean {
        if (str.length > available) return false
        for (n in 0 until str.length) if (this.str[offset + n] != str[n]) return false
        offset += str.length
        return true
    }
    fun read(count: Int): String = this.str.substring(offset, (offset + count).coerceAtMost(length)).also { this.offset += it.length }
    fun readInt(count: Int): Int = read(count).toInt()
    fun tryReadInt(count: Int): Int? = read(count).toIntOrNull()
    fun tryReadDouble(count: Int): Double? = read(count).replace(',', '.').toDoubleOrNull()

    fun tryReadDouble(): Double? {
        var numCount = 0
        var num = 0
        var denCount = 0
        var den = 0
        var decimals = false
        loop@while (hasMore) {
            when (val pc = peekChar()) {
                ',' -> {
                    if (numCount == 0) {
                        return null
                    }
                    decimals = true
                    readChar()
                }
                in '0'..'9' -> {
                    val c = readChar()
                    if (decimals) {
                        denCount++
                        den *= 10
                        den += (c - '0')
                    } else {
                        numCount++
                        num *= 10
                        num += (c - '0')
                    }
                }
                else -> {
                    break@loop
                }
            }
        }
        if (numCount == 0) {
            return null
        }
        return num.toDouble() + (den.toDouble() * 10.0.pow(-denCount))
    }
}