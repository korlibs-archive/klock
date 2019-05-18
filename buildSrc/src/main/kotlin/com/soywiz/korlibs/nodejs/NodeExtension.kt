package com.soywiz.korlibs.nodejs

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.AbstractTask
import java.io.File
import com.soywiz.korlibs.*
import org.gradle.api.tasks.TaskAction
import java.net.URL

open class NodePlugin : Plugin<Project> {
    companion object {
        val NODE_SETUP_TASK_NAME = "nodeSetup"
    }
    override fun apply(project: Project) {
        val node = NodeExtension(project)

        project.extensions.add(NodeExtension::class.java, "node",node)

        //project.tasks.create<Task>("nodeSetup") { }
        project.tasks.create<Task>(NodePlugin.NODE_SETUP_TASK_NAME) {
            outputs.file { node.localArchiveFile }
            //project.afterEvaluate { outputs.file(node.localArchiveFile) }
            doLast {
                if (node.download) {
                    if (!node.localArchiveFile.exists()) {
                        project.downloadFile(node.downloadArchiveUrl, node.localArchiveFile)
                    }
                    if (!node.nodeCommand.first().exists()) {
                        project.extractArchive(node.localArchiveFile, node.workDir)
                    }
                }
            }
        }

        project.afterEvaluate {
            project.nodeExec(project.node.npmCli, "--prefix", project.node.npmPrefix, "install", "mocha@5.2.0")
        }

        //project.afterEvaluate {
        //    println(node.nodeCommand)
        //    project.nodeExec("--version")
        //}
    }
}

val Project.node get() = this.extensions.getByType(NodeExtension::class.java)

class NodeTarget(val base: String, val ext: String, val nodeExe: String = "node") {
    val file = "$base.$ext"
}

open class NodeExtension(val project: Project) {
    // https://nodejs.org/dist/v8.11.4/
    var version: String = "8.11.4"
    var download: Boolean = false
    var workDir: File = File(".")
    var npmWorkDir: File = File("npm")
    var yarnWorkDir: File = File("yarn")
    var nodeModulesDir: File = File("node_modules")

    val winTarget get() = NodeTarget("node-v$version-win-x64", "zip", "node.exe")
    val linuxTarget get() = NodeTarget("node-v$version-linux-x64", "tar.gz")
    val macTarget get() = NodeTarget("node-v$version-darwin-x64", "tar.gz")

    val target get() = when {
        OsInfo.isWindows -> winTarget
        OsInfo.isMac -> macTarget
        else -> linuxTarget
    }

    val archiveFile get() = target.file

    val localArchiveFile get() = workDir[archiveFile]
    val downloadArchiveUrl get() = URL("$downloadBaseUrl/$archiveFile")

    val downloadBaseUrl get() = "https://nodejs.org/dist/v$version"

    val nodeRootDir get() = workDir[target.base]
    val nodeCommand get() = listOf(nodeRootDir["bin/${target.nodeExe}"])
    val npmCli get() = nodeRootDir["lib/node_modules/npm/bin/npm-cli.js"]
    val npmCommand get() = nodeCommand + listOf(npmCli)

    val npmPrefix get() = if (nodeModulesDir.name == "node_modules") nodeModulesDir.parentFile else nodeModulesDir
}

fun Project.nodeExec(vararg args: Any, workingDir: File? = null) {
    val node = project.node
    project.exec {
        if (workingDir != null) it.workingDir = workingDir
        val commandLineArgs = node.nodeCommand + args.map { "$it" }
        println("Executing<$workingDir>... ${commandLineArgs.joinToString(" ")}")
        //it.environment("NPM_CONFIG_PREFIX", node.npmPrefix)
        it.setCommandLine(commandLineArgs)
    }
}

open class AbstractNodeTask : AbstractTask() {
    var _args: List<Any> = listOf()
    var _workingDir: File? = null
    fun setWorkingDir(file: File) {
        _workingDir = file
    }

    fun setArgs(args: List<Any>) {
        this._args = args
    }

    open fun getArgs(): List<Any> {
        return this._args;
    }

    init {
        dependsOn(NodePlugin.NODE_SETUP_TASK_NAME)
    }

    @TaskAction
    fun execute() {
        project.nodeExec(*getArgs().toTypedArray(), workingDir = _workingDir)
    }
}

open class NodeTask : AbstractNodeTask() {
    open var _script: File? = null

    fun setScript(file: File) {
        _script = file

    }

    override fun getArgs(): List<Any> {
        return listOf(_script ?: error("script not specified")) + super.getArgs()
    }
}

open class NpmTask : NodeTask() {
    override fun getArgs(): List<Any> {
        return listOf(project.node.npmCli, "--prefix", project.node.npmPrefix) + this._args
    }
}

