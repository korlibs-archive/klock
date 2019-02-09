package com.soywiz.korlibs

import com.soywiz.korlibs.modules.*
import com.soywiz.korlibs.targets.*
import org.gradle.api.*
import java.io.*

class KorlibsPlugin : Plugin<Project> {
    override fun apply(project: Project) = project {
        val korlibs = KorlibsExtension(this)
        extensions.add("korlibs", korlibs)

        plugins.apply("kotlin-multiplatform")
        plugins.apply("com.moowork.node")

        configureKorlibsRepos()

        // Platforms
        configureTargetCommon()
        configureTargetAndroid()
        configureTargetNative()
        configureTargetJavaScript()
        configureTargetJVM()

        // Publishing
        configurePublishing()
    }
}

class KorlibsExtension(val project: Project) {
    var hasAndroid = (System.getProperty("sdk.dir") != null) || (System.getenv("ANDROID_HOME") != null)

    init {
        if (!hasAndroid) {
            val trySdkDir = File(System.getProperty("user.home") + "/Library/Android/sdk")
            if (trySdkDir.exists()) {
                File(project.rootDir, "local.properties").writeText("sdk.dir=${trySdkDir.absolutePath}")
                hasAndroid = true
            }
        }
    }

    fun dependencyProject(name: String) = project {
        dependencies {
            add("commonMainApi", project(name))
        }
    }

    val ALL_TARGETS = listOf("android", "iosArm64", "iosArm32", "iosX64", "js", "jvm", "linuxX64", "macosX64", "mingwX64", "metadata")

    @JvmOverloads
    fun dependencyMulti(group: String, name: String, version: String, targets: List<String> = ALL_TARGETS, suffixCommonRename: Boolean = false, androidIsJvm: Boolean = false) = project {
        dependencies {
            for (target in targets) {
                val base = when (target) {
                    "metadata" -> "common"
                    else -> target
                }
                val suffix = when {
                    target == "android" && androidIsJvm -> "-jvm"
                    target == "metadata" && suffixCommonRename -> "-common"
                    else -> "-${target.toLowerCase()}"
                }

                val packed = "$group:$name$suffix:$version"
                add("${base}MainApi", packed)
                add("${base}TestImplementation", packed)
            }
        }
    }

    @JvmOverloads
    fun dependencyMulti(dependency: String, targets: List<String> = ALL_TARGETS) {
        val (group, name, version) = dependency.split(":", limit = 3)
        return dependencyMulti(group, name, version, targets)
    }

    @JvmOverloads
    fun exposeVersion(name: String = project.name) {
        project.projectDir["src/commonMain/kotlin/com/soywiz/$name/internal/${name.capitalize()}Version.kt"].text = """
            package com.soywiz.$name.internal

            internal const val ${name.toUpperCase()}_VERSION = "${project.version}"
        """.trimIndent()
    }
}

val Project.korlibs get() = extensions.getByType(KorlibsExtension::class.java)
fun Project.korlibs(callback: KorlibsExtension.() -> Unit) = korlibs.apply(callback)
val Project.hasAndroid get() = korlibs.hasAndroid
