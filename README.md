<p align="center">
    <img alt="Klock" src="/assets/klock_256.png" />
</p>

<p align="center">
    Klock is a Date & Time library for Multiplatform Kotlin 1.3.
</p>

<p align="center">
    It is designed to be as allocation-free as possible using Kotlin inline classes,
    to be consistent and portable across targets since all the code is written in Common Kotlin,
    and to provide an API that is powerful, fun and easy to use.
</p>

<!-- BADGES -->

<p align="center">
    <a href="https://travis-ci.org/korlibs/klock"><img alt="Build Status" src="https://travis-ci.org/korlibs/klock.svg?branch=master" /></a>
    <a href="http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22klock%22"><img alt="Maven Version" src="https://img.shields.io/github/tag/korlibs/klock.svg?style=flat&label=maven" /></a>
    <a href="https://slack.soywiz.com/"><img alt="Slack" src="https://img.shields.io/badge/chat-on%20slack-green?style=flat&logo=slack" /></a>
</p>

<!-- /BADGES -->

<!-- SUPPORT -->

<h2 align="center">Support klock</h2>

<p align="center">
If you like klock, or want your company logo here, please consider <a href="https://github.com/sponsors/soywiz">becoming a sponsor ★</a>,<br />
in addition to ensure the continuity of the project, you will get exclusive content.
</p>

<!-- /SUPPORT -->

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

(Compiled and tested with Gradle 5.4.1, JVM 12.0.1 and Kotlin 1.3.31).
Starting with Klock 1.4.0, the library is available at jcenter.

```groovy
def klockVersion = "1.7.3"

repositories {
    jcenter()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation "com.soywiz.korlibs.klock:klock:$klockVersion" // Common 
            }
        }
    }
}
```

#### `settings.gradle`

```groovy
enableFeaturePreview('GRADLE_METADATA')
```

### Use with Kotlin-JVM

```groovy
def klockVersion = "1.7.3"

repositories {
    jcenter()
}

dependencies {
    implementation "com.soywiz.korlibs.klock:klock-jvm:$klockVersion"
}
```

### Versions

| Klock  | Kotlin | Gradle Metadata  |
|--------|--------|------------------|
| 1.7.0  | 1.3.50 | 1.0              |
| 1.6.0  | 1.3.50 | 1.0              |
| 1.5.0  | 1.3.40 | 1.0              |
| 1.4.0  | 1.3.21 | 1.0              |
