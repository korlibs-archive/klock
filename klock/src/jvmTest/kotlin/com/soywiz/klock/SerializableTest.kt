package com.soywiz.klock

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.test.Test
import kotlin.test.assertTrue

class SerializableTest {
    @Test
    fun test() {
        val os = ByteArrayOutputStream()
        val oos = ObjectOutputStream(os)
        val time = 1_000_000L
        oos.writeObject(DateTimeTz.fromUnixLocal(time))
        val bao = os.toByteArray()
        val obj = ObjectInputStream(ByteArrayInputStream(bao)).readObject()
        assertTrue { obj is DateTimeTz }
    }
}
