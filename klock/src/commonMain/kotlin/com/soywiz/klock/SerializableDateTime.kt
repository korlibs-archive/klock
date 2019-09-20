package com.soywiz.klock

data class SerializableDateTime(val dateTime: DateTime) {
	override fun toString(): String = dateTime.toString()
}

fun DateTime.serializable() = SerializableDateTime(this)
