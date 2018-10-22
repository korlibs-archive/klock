package com.soywiz.klock

fun SimplerDateFormat.parseLong(str: String) = parse(str).base.unixLong
fun SimplerDateFormat.parseDouble(str: String) = parse(str).base.unixDouble
fun SimplerDateFormat.parseDoubleOrNull(str: String) = tryParse(str)?.base?.unixDouble

