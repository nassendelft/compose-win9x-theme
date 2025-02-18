import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    `maven-publish`
    signing
}

val publishVersionProp = findProperty("publishVersion")

group = "nl.ncaj.theme.win9x"
version = publishVersionProp?.toString()
    ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.io.core)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.common)
        }
    }

    jvmToolchain(17)
}

android {
    namespace = "nl.ncaj.theme.win9x"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

publishing {
    publications {
        named<MavenPublication>("jvm") {
            // generate empty javadoc jar files to satisfy sonatype requirements
            // https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
            val javaDocTask = tasks.register<Jar>("javaDocJvm") {
                archiveBaseName.set("${project.name}-jvm")
                archiveClassifier.set("javadoc")
            }
            artifact(javaDocTask)

            pom {
                name.set("${groupId}:${artifactId}")
                description.set("Jetpack compose Win9X Theme")
                url.set("https://win9x-compose.ncaj.nl/")

                licenses {
                    license {
                        name = "GPL-3.0"
                        url = "https://spdx.org/licenses/GPL-3.0.html"
                    }
                }

                developers {
                    developer {
                        id = "nassendelft"
                        name = "Nick Assendelft"
                        email = "nick@nickassendelft.nl"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/nassendelft/compose-win9x-theme.git"
                    developerConnection = "scm:git:ssh://github.com/nassendelft/compose-win9x-theme.git"
                    url = "https://github.com/nassendelft/compose-win9x-theme"
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/nassendelft/compose-win9x-theme")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
        maven {
            name = "SonatypeIntermediate"
            url = uri(layout.buildDirectory.dir("repository"))
        }
    }
}

signing {
    useInMemoryPgpKeys(System.getenv("PGP_KEY"), System.getenv("PGP_PASSWORD"))
    sign(publishing.publications["jvm"])
}

val publishJvmPublicationToSonatypeIntermediateRepository by tasks.getting

val sonatypeZip = tasks.register<Zip>("sonatypeZip") {
    dependsOn(publishJvmPublicationToSonatypeIntermediateRepository)
    from(layout.buildDirectory.dir("repository"))
    archiveBaseName.set("${project.name}-jvm")
    destinationDirectory.set(layout.buildDirectory.dir("publications"))
}

tasks.register("publishSonatype") {
    group = "publishing"
    dependsOn(sonatypeZip)

    val publishAuthToken = System.getenv("SONATYPE_AUTH_TOKEN")

    doLast {
        val boundary = "boundary"
        val url = URL("https://central.sonatype.com/api/v1/publisher/upload?publishingType=AUTOMATIC")
        val file = sonatypeZip.get().outputs.files.first()
        (url.openConnection() as HttpURLConnection).apply {
            doOutput = true

            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer $publishAuthToken")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=${boundary}")
            outputStream.use {
                it.write("--$boundary\r\n".toByteArray())
                it.write("--$boundary\r\n".toByteArray())
                it.write("Content-Disposition: form-data; name=\"bundle\"; filename=\"${file.name}\"\r\n".toByteArray())
                it.write("Content-Type: application/octet-stream\r\n".toByteArray())
                it.write("\r\n".toByteArray())
                file.inputStream().copyTo(it)
                it.write("\r\n".toByteArray())
                it.write("--$boundary".toByteArray())
            }

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                println(inputStream.use { it.readAllBytes() }.toString())
            } else {
                error("Publishing to Sonatype failed with response code $responseCode")
            }
        }
    }
}
