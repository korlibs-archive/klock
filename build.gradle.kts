import com.moowork.gradle.node.*
import com.moowork.gradle.node.npm.*
import com.moowork.gradle.node.task.*
import groovy.util.*
import groovy.xml.*
import org.apache.tools.ant.taskdefs.condition.Os
import org.jetbrains.kotlin.backend.common.*
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.*

buildscript {
    var hasAndroid = (System.getProperty("sdk.dir") != null) || (System.getenv("ANDROID_HOME") != null)

    if (!hasAndroid) {
        val trySdkDir = File(System.getProperty("user.home") + "/Library/Android/sdk")
        if (trySdkDir.exists()) {
            File(rootDir, "local.properties").writeText("sdk.dir=${trySdkDir.absolutePath}")
            hasAndroid = true
        }
    }

    if (hasAndroid) {
        repositories {
            mavenLocal()
            mavenCentral()
            google()
        }
        dependencies {
            classpath("com.android.tools.build:gradle:3.3.0")
            classpath(kotlin("gradle-plugin", version = "1.3.20"))
        }
    }
}

// @TODO: Can we pass information from buildscript to here with kotlin-dsl?
var hasAndroid = (System.getProperty("sdk.dir") != null) || (System.getenv("ANDROID_HOME") != null) || File(
    rootDir,
    "local.properties"
).exists()

allprojects {
    repositories {
        mavenLocal()
        maven { url = uri("https://dl.bintray.com/soywiz/soywiz") }
        jcenter()
        google()
    }
}

plugins {
    id("kotlin-multiplatform").version("1.3.20")
    id("com.moowork.node").version("1.2.0")
    id("maven-publish")
}

allprojects {
    repositories {
        mavenLocal()
        maven { url = uri("https://dl.bintray.com/soywiz/soywiz") }
        jcenter()
        google()
    }

    if (project.file("build.project.gradle").exists()) {
        apply(from = project.file("build.project.gradle"))
    }
}

operator fun File.get(name: String) = File(this, name)
var File.text get() = this.readText(); set(value) = run { this.writeText(value) }
val NamedDomainObjectCollection<KotlinTarget>.js get() = this["js"] as KotlinOnlyTarget<KotlinJsCompilation>
val NamedDomainObjectCollection<KotlinTarget>.jvm get() = this["jvm"] as KotlinOnlyTarget<KotlinJvmCompilation>
val NamedDomainObjectCollection<KotlinTarget>.metadata get() = this["metadata"] as KotlinOnlyTarget<KotlinCommonCompilation>

val <T : KotlinCompilation<*>> NamedDomainObjectContainer<T>.main get() = this["main"]
val <T : KotlinCompilation<*>> NamedDomainObjectContainer<T>.test get() = this["test"]

subprojects {
    if (project.name == "template") return@subprojects

    apply(plugin = "kotlin-multiplatform")
    apply(plugin = "com.moowork.node")

    if (hasAndroid) {
        apply(plugin = "com.android.library")
        //apply(plugin = "org.jetbrains.kotlin.android")
        //apply(plugin = "org.jetbrains.kotlin.android.extensions")
        extensions.getByType<com.android.build.gradle.LibraryExtension>().apply {
            compileSdkVersion(28)
            defaultConfig {
                minSdkVersion(18)
                targetSdkVersion(28)
            }
        }
    }

    kotlin {
        if (hasAndroid) {
            android {
                publishLibraryVariants("release", "debug")
            }
        }

        iosX64()
        iosArm32()
        iosArm64()
        macosX64()
        linuxX64()
        mingwX64()
        jvm()
        js {
            compilations.all {
                kotlinOptions {
                    languageVersion = "1.3"
                    sourceMap = true
                    metaInfo = true
                    moduleKind = "umd"
                }
            }
        }

        // Only enable when loaded in IDEA (we use a property for detection). In CLI that would produce an "expect" error.

        if (System.getProperty("idea.version") != null) {
            when {
                Os.isFamily(Os.FAMILY_WINDOWS) -> run { mingwX64("nativeCommon"); mingwX64("nativePosix") }
                Os.isFamily(Os.FAMILY_MAC) -> run { macosX64("nativeCommon"); macosX64("nativePosix") }
                else -> run { linuxX64("nativeCommon"); linuxX64("nativePosix") }
            }
        }

        sourceSets {
            val jvmMain = this["jvmMain"]
            val nativeCommonMain = create("nativeCommonMain")
            val nativeCommonTest = create("nativeCommonTest")
            val nativePosixMain = create("nativePosixMain")
            if (hasAndroid) {
                this.maybeCreate("androidMain").apply {
                    dependsOn(jvmMain)
                }
                this.maybeCreate("androidTest").apply {
                    dependsOn(jvmMain)
                }
            }
            maybeCreate("mingwX64Main").apply {
                dependsOn(nativeCommonMain)
            }
            maybeCreate("mingwX64Test").apply {
                dependsOn(nativeCommonTest)
            }

            val iosTargets = listOf(this["iosX64Main"], this["iosArm32Main"], this["iosArm64Main"])

            configure(iosTargets + listOf(this["macosX64Main"], this["linuxX64Main"])) {
                dependsOn(nativeCommonMain)
                dependsOn(nativePosixMain)
            }

            configure(iosTargets + listOf(this["macosX64Test"], this["linuxX64Test"])) {
                dependsOn(nativeCommonTest)
            }

            val iosCommonMain = create("iosCommonMain")
            val iosCommonTest = create("iosCommonTest")

            configure(iosTargets) { dependsOn(iosCommonMain) }
            configure(iosTargets) { dependsOn(iosCommonTest) }

        }
    }

    dependencies {
        commonMainImplementation("org.jetbrains.kotlin:kotlin-stdlib-common")
        commonTestImplementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
        commonTestImplementation("org.jetbrains.kotlin:kotlin-test-common")

        if (hasAndroid) {
            add("androidMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test")
            add("androidTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit")
        }

        add("jsMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-js")
        add("jsTestImplementation", "org.jetbrains.kotlin:kotlin-test-js")

        add("jvmMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        add("jvmTestImplementation", "org.jetbrains.kotlin:kotlin-test")
        add("jvmTestImplementation", "org.jetbrains.kotlin:kotlin-test-junit")
    }

    // Javascript test configuration
    val korlibsDir = File(System.getProperty("user.home"), ".korlibs").apply { mkdirs() }

    run {
        extensions.getByType<NodeExtension>().apply {
            version = "8.11.4"
            download = true
            workDir = korlibsDir["nodejs"]
            npmWorkDir = korlibsDir["npm"]
            yarnWorkDir = korlibsDir["yarn"]
            nodeModulesDir = korlibsDir["node_modules"]
        }

        // Fix for https://github.com/srs/gradle-node-plugin/issues/301
        repositories.whenObjectAdded {
            if (this is IvyArtifactRepository) {
                metadataSources {
                    artifact()
                }
            }
        }

        // Small optimization
        tasks {
            this["nodeSetup"].onlyIf { !korlibsDir["nodejs"].exists() }
        }

    }


    val jsCompilations = kotlin.targets.js.compilations


    tasks.create<NpmTask>("installMocha") {
        onlyIf { !node.nodeModulesDir["mocha"].exists() }
        setArgs(listOf("install", "mocha@5.2.0"))
    }
    tasks.create<DefaultTask>("populateNodeModules") {
        doLast {
            copy {
                from("${node.nodeModulesDir}")
                from(jsCompilations.main.output.allOutputs)
                from(jsCompilations.test.output.allOutputs)
                for (it in jsCompilations.test.runtimeDependencyFiles) {
                    if (it.exists() && !it.isDirectory) {
                        from(zipTree(it.absolutePath).matching { include("*.js") })
                    }
                }
                afterEvaluate {
                    for (sourceSet in kotlin.sourceSets) {
                        from(sourceSet.resources)
                    }
                }
                into("$buildDir/node_modules")
            }
        }
    }
    tasks.create<NodeTask>("runMocha") {
        dependsOn(jsCompilations.test.compileKotlinTaskName, "installMocha", "populateNodeModules")
        setScript(file("$buildDir/node_modules/mocha/bin/mocha"))
        setWorkingDir(file("$buildDir/node_modules"))
        setArgs(listOf("--timeout", "15000", "${project.name}_test.js"))
    }
    tasks.create<NpmTask>("jsInstallMochaHeadlessChrome") {
        onlyIf { !node.nodeModulesDir["mocha-headless-chrome"].exists() }
        setArgs(listOf("install", "mocha-headless-chrome@2.0.1"))
    }
    tasks.create<NodeTask>("jsTestChrome") {
        dependsOn(jsCompilations.test.compileKotlinTaskName, "jsInstallMochaHeadlessChrome", "installMocha", "populateNodeModules")
        doFirst {
            buildDir["node_modules/tests.html"].text = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Mocha Tests</title>
                    <meta charset="utf-8">
                    <link rel="stylesheet" href="mocha/mocha.css">
                    <script src="https://cdnjs.cloudflare.com/ajax/libs/require.js/2.3.6/require.min.js"></script>
                </head>
                <body>
                <div id="mocha"></div>
                <script src="mocha/mocha.js"></script>
                <script>
                    requirejs.config({'baseUrl': '.', 'paths': { 'tests': '${project.name}_test' }});
                    mocha.setup('bdd');
                    require(['tests'], function() { mocha.run(); });
                </script>
                </body>
                </html>
            """.trimIndent()
        }
        setScript(node.nodeModulesDir["/mocha-headless-chrome/bin/start"])
        setArgs(listOf("-f", "$buildDir/node_modules/tests.html", "-a", "no-sandbox", "-a", "disable-setuid-sandbox", "-a", "allow-file-access-from-files"))
    }


    afterEvaluate {
        for (target in listOf("macosX64", "linuxX64", "mingwX64")) {
            val taskName = "copyResourcesToExecutable_${target}"
            val targetTestTask = tasks.getByName("${target}Test")
            tasks {
                create<Copy>(taskName) {
                    for (sourceSet in kotlin.sourceSets) {
                        from(sourceSet.resources)
                    }
                    into(File(targetTestTask.inputs.properties["executable"].toString()).parentFile)
                }
            }

            targetTestTask.dependsOn(taskName)
        }
    }

    // Include resources from JS and Metadata (common) into the JS JAR
    for (target in listOf(kotlin.targets.js, kotlin.targets.metadata)) {
        for (sourceSet in target.compilations.main.kotlinSourceSets) {
            for (it in sourceSet.resources.srcDirs) {
                tasks.getByName<Jar>("jsJar").from(it)
            }
        }
    }

    // Only run JS tests if not in windows
    if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
        tasks.getByName("jsTest").dependsOn("runMocha")
    }

    group = "com.soywiz"
    version = properties["projectVersion"].toString()

    // Publishing
    val publishUser = (rootProject.findProperty("BINTRAY_USER") ?: project.findProperty("bintrayUser") ?: System.getenv("BINTRAY_USER"))?.toString()
    val publishPassword = (rootProject.findProperty("BINTRAY_KEY") ?: project.findProperty("bintrayApiKey") ?: System.getenv("BINTRAY_API_KEY"))?.toString()

    apply(plugin = "maven-publish")

    if (publishUser != null && publishPassword != null) {
        extensions.getByType<PublishingExtension>().apply {
            repositories {
                maven {
                    credentials {
                        username = publishUser
                        setPassword(publishPassword)
                    }
                    url = uri("https://api.bintray.com/maven/soywiz/soywiz/${project.property("project.package")}/")
                }
            }
            afterEvaluate {
                configure(publications) {
                    this as MavenPublication
                    pom.withXml {
                        this.asNode().apply {
                            appendNode("name", project.name)
                            appendNode("description", project.property("project.description"))
                            appendNode("url", project.property("project.scm.url"))
                            appendNode("licenses").apply {
                                appendNode("license").apply {
                                    appendNode("name").setValue(project.property("project.license.name"))
                                    appendNode("url").setValue(project.property("project.license.url"))
                                }
                            }
                            appendNode("scm").apply {
                                appendNode("url").setValue(project.property("project.scm.url"))
                            }

                            // Changes runtime -> compile in Android's AAR publications
                            if (pom.packaging == "aar") {
                                val nodes = this.getAt(QName("dependencies")).getAt("dependency").getAt("scope")
                                for (node in nodes) {
                                    (node as Node).setValue("compile")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Headless testing on JVM (so we can use GWT)
    tasks.getByName<Test>("jvmTest") {
        jvmArgs = (jvmArgs ?: arrayListOf()) + arrayListOf("-Djava.awt.headless=true")
    }
}

