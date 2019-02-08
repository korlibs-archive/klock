package com.soywiz.korlibs.modules

import groovy.util.*
import groovy.xml.*
import org.gradle.api.*
import org.gradle.api.publish.*
import org.gradle.api.publish.maven.*

fun Project.configurePublishing() {
    // Publishing
    val publishUser = (rootProject.findProperty("BINTRAY_USER") ?: project.findProperty("bintrayUser") ?: System.getenv("BINTRAY_USER"))?.toString()
    val publishPassword = (rootProject.findProperty("BINTRAY_KEY") ?: project.findProperty("bintrayApiKey") ?: System.getenv("BINTRAY_API_KEY"))?.toString()

    plugins.apply("maven-publish")

    if (publishUser != null && publishPassword != null) {
        val publishing = extensions.getByType(PublishingExtension::class.java)
        publishing.apply {
            repositories {
                it.maven {
                    it.credentials {
                        it.username = publishUser
                        it.setPassword(publishPassword)
                    }
                    it.url = uri("https://api.bintray.com/maven/soywiz/soywiz/${project.property("project.package")}/")
                }
            }
            afterEvaluate {
                configure(publications) {
                    this as MavenPublication
                    pom.withXml {
                        it.asNode().apply {
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
}
