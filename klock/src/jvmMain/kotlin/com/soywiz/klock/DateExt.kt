package com.soywiz.klock

import java.util.*

@Deprecated("", replaceWith = ReplaceWith("", "com.soywiz.klock.jvm.toDateTime"), level = DeprecationLevel.HIDDEN)
fun Date.toDateTime() = DateTime(this.time)

@Deprecated("", replaceWith = ReplaceWith("", "com.soywiz.klock.jvm.toDate"), level = DeprecationLevel.HIDDEN)
fun DateTime.toDate() = Date(this.unixMillisLong)
