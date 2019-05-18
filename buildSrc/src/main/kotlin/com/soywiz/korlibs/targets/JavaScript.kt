package com.soywiz.korlibs.targets

import com.soywiz.korlibs.*
import org.gradle.api.*
import org.gradle.api.artifacts.repositories.*
import com.soywiz.korlibs.modules.staticHttpServer
import com.soywiz.korlibs.nodejs.*
import org.apache.tools.ant.taskdefs.condition.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.api.tasks.testing.*
import java.io.*

fun Project.configureTargetJavaScript() {
    gkotlin.apply {
        js {
            compilations.all {
                it.kotlinOptions.apply {
                    languageVersion = "1.3"
                    sourceMap = true
                    metaInfo = true
                    moduleKind = "umd"
                }
            }
        }
    }

    dependencies.apply {
        add("jsMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-js")
        add("jsTestImplementation", "org.jetbrains.kotlin:kotlin-test-js")
    }

    // Javascript test configuration
    val korlibsDir = korlibs.korlibsDir

    val node = extensions.getByType(NodeExtension::class.java)
    val globalNodeModulesDir: File = node.nodeModulesDir
    val localNodeModulesDir = buildDir["node_modules"]

    val jsCompilations = gkotlin.targets.js.compilations

    val installMocha = tasks.create<NpmTask>("installMocha") {
        onlyIf { !node.nodeModulesDir["mocha"].exists() }
        setArgs(listOf("install", "mocha@5.2.0"))
    }

    val jsInstallMochaHeadlessChrome = tasks.create<NpmTask>("jsInstallMochaHeadlessChrome") {
        onlyIf { !node.nodeModulesDir["mocha-headless-chrome"].exists() }
        setArgs(listOf("install", "mocha-headless-chrome@2.0.1"))
    }

    val populateNodeModules = tasks.create<Copy>("populateNodeModules") {
        dependsOn(jsCompilations.main.compileKotlinTask, jsCompilations.test.compileKotlinTask, installMocha)
        //println("jsCompilations.main.output.allOutputs: ${jsCompilations.main.output.allOutputs.files}")
        //println("jsCompilations.test.output.allOutputs: ${jsCompilations.test.output.allOutputs.files}")
        //println("jsCompilations.test.runtimeDependencyFiles.files: ${jsCompilations.test.runtimeDependencyFiles.files}")

        from({ globalNodeModulesDir })
        from({ jsCompilations.main.output.allOutputs })
        from({ jsCompilations.test.output.allOutputs })
        from({
            jsCompilations.test.runtimeDependencyFiles.flatMap { f ->
                if (f.exists() && !f.isDirectory) {
                    listOf(zipTree(f.absolutePath).matching { it.include("*.js") })
                } else {
                    listOf()
                }
            }
        })
        from({
            gkotlin.sourceSets.flatMap { sourceSet ->
                listOf(sourceSet.resources)
            }
        })

        //println(node.nodeModulesDir)
        //println("$buildDir/node_modules")
        into(localNodeModulesDir)
    }

    val jsTestNode = tasks.create<NodeTask>("jsTestNode") {
        dependsOn(populateNodeModules)

        val resultsFile = buildDir["node-results/results.json"]
        setScript(file("$buildDir/node_modules/mocha/bin/mocha"))
        setWorkingDir(file("$buildDir/node_modules"))
        setArgs(listOf("-c", "--timeout", "15000", "${project.name}_test.js", "-o", resultsFile))
        inputs.files(jsCompilations.test.compileKotlinTask.outputFile, jsCompilations.main.compileKotlinTask.outputFile)
        outputs.file(resultsFile)
    }

    val jsTestChrome = tasks.create<Task>("jsTestChrome") {
        dependsOn(populateNodeModules, jsInstallMochaHeadlessChrome)

        val resultsFile = buildDir["chrome-results/results.json"]
        inputs.files(jsCompilations.test.compileKotlinTask.outputFile, jsCompilations.main.compileKotlinTask.outputFile)
        outputs.file(resultsFile)

        doLast {
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

            staticHttpServer(buildDir["node_modules"]) { httpServer ->
                nodeExec(
                    node.nodeModulesDir["mocha-headless-chrome/bin/start"],
                    "-f", "http://127.0.0.1:${httpServer.address.port}/tests.html", "-o", resultsFile
                )
            }
        }
    }

    // Include resources from JS and Metadata (common) into the JS JAR
    val jsJar = tasks.getByName("jsJar") as Jar
    val jsTest = tasks.getByName("jsTest") as Test

    for (target in listOf(kotlin.targets.js, kotlin.targets.metadata)) {
        //for (target in listOf(kotlin.targets.js)) {
        for (sourceSet in target.compilations.main.kotlinSourceSets) {
            for (it in sourceSet.resources.srcDirs) {
                jsJar.from(it)
            }
        }
    }

    // Only run JS tests if not in windows
    if (!Os.isFamily(Os.FAMILY_WINDOWS)) {
        jsTest.dependsOn(jsTestNode)

        // Except on travis (we have a separate target for it)
        if (System.getenv("TRAVIS") == null) {
            jsTest.dependsOn(jsTestChrome)
        }
    }
}

