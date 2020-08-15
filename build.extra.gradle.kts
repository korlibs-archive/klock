//import com.soywiz.korlibs.*

//apply<com.soywiz.korlibs.KorlibsPlugin>()

/*
subprojects {
    afterEvaluate {
        tasks {
            //val jsJar by existing(Jar::class)
            val npmExtract by creating(Copy::class) {
                afterEvaluate {
                    dependsOn(jsJar)
                    from(zipTree(jsJar.get().outputs.files.toList().first()))
                    //from(new File(project.projectDir, "src/jsMain/npm/package.json")) { filter(org.apache.tools.ant.filters.ReplaceTokens, tokens: [VERSION: version, KOTLIN_VERSION: "1.3.50"], beginToken: "%%%", endToken: "!!!") }
                    from(File(rootDir, "README.md"))
                    from(File(rootDir, "npm/package.json")) {
                        filter(
                            mapOf(
                                //"tokens" to mapOf("VERSION" to version, "KOTLIN_VERSION" to korlibs.KORLIBS_KOTLIN_VERSION),
                                "tokens" to mapOf("VERSION" to version, "KOTLIN_VERSION" to "1.3.70"),
                                "beginToken" to "%%%",
                                "endToken" to "!!!"
                            ),
                            org.apache.tools.ant.filters.ReplaceTokens::class.java
                        )
                    }
                    into(File(buildDir, "npmPackage"))
                }
            }
            val npmPublish by creating(Exec::class) {
                dependsOn(npmExtract)
                workingDir("${buildDir}/npmPackage")
                commandLine("npm", "publish", "--access", "public")
            }
        }
    }
}
*/
