# ![Klock](/assets/klock_256.png)

Klock is a Date & Time library for Multiplatform Kotlin 1.3.

It is designed to be as allocation-free as possible using Kotlin inline classes,
to be consistent and portable across targets since all the code is written in Common Kotlin,
and to provide an API that is powerful, fun and easy to use.

[![Build Status](https://travis-ci.org/korlibs/klock.svg?branch=master)](https://travis-ci.org/korlibs/klock)
[![Maven Version](https://img.shields.io/github/tag/korlibs/klock.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22klock%22)
[![Gitter](https://img.shields.io/gitter/room/korlibs/korlibs.svg)](https://gitter.im/korlibs/Lobby)

### Full Documentation: <https://korlibs.soywiz.com/klock/>

### Some samples:

```kotlin
val now = DateTime.now()
val duration = 1.seconds
val later = now + 1.months + duration
val is2018Leap = Year(2018).isLeap
val daysInCurrentMonth = now.yearMonth.days
val daysInNextMonth = (now.yearMonth + 1.months).days

```

### Usage with gradle:

```
def klockVersion = "1.4.0"

repositories {
    maven { url "https://dl.bintray.com/soywiz/soywiz" }
}

dependencies {
    // For multiplatform projects, since klock is published without metadata
    commonMainApi "com.soywiz:klock-metadata:$klockVersion" // Common 
    jvmMainApi "com.soywiz:klock-jvm:$klockVersion" // JVM
    jsMainApi "com.soywiz:klock-js:$klockVersion" // JavaScript
    androidMainApi "com.soywiz:klock-android:$klockVersion" // Android
    iosX64MainApi "com.soywiz:klock-iosx64:$klockVersion" // iOS Simulator
    iosArm32MainApi "com.soywiz:klock-iosarm32:$klockVersion" // Older iOS 32-bit devices
    iosArm64MainApi "com.soywiz:klock-iosarm64:$klockVersion" // Newer iOS 64-bit devices
    macosX64MainApi "com.soywiz:klock-macosx64:$klockVersion" // MacOS
    linuxX64MainApi "com.soywiz:klock-linuxx64:$klockVersion" // Linux x64
    mingwX64MainApi "com.soywiz:klock-mingwx64:$klockVersion" // Windows x64
}
```

