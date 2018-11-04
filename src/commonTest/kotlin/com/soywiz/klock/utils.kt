package com.soywiz.klock

fun PatternDateFormat.parseLong(str: String) = parse(str).base.unixMillisLong
fun PatternDateFormat.parseDouble(str: String) = parse(str).base.unixMillisDouble
fun PatternDateFormat.parseDoubleOrNull(str: String) = tryParse(str)?.base?.unixMillisDouble

