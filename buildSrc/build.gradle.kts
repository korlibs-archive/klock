import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.21"
}
buildscript {
    val kotlin_version = "1.3.21"
    extra["kotlin_version"] = kotlin_version
    repositories {
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

val kotlin_version = extra["kotlin_version"]

apply(plugin = "kotlin")

repositories {
    mavenLocal()
    mavenCentral()
    google()
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    testImplementation("junit:junit:4.12")
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    implementation("com.android.tools.build:gradle:3.3.1")
    implementation("com.moowork.gradle:gradle-node-plugin:1.2.0")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
