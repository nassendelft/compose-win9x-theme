import com.android.build.gradle.internal.tasks.factory.dependsOn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    `maven-publish`
    signing
}

val publishVersionProp = findProperty("publishVersion")

version = publishVersionProp?.toString()
    ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"))

publishing {
    publications.withType<MavenPublication>().all {
        // generate empty javadoc jar files to satisfy sonatype requirements
        // https://central.sonatype.org/publish/requirements/#supply-javadoc-and-sources
        val javaDocTask = tasks.register<Jar>("javaDoc${name.capitalize()}") {
            archiveBaseName.set("${project.name}-${name.capitalize()}")
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

signing {
    useInMemoryPgpKeys(System.getenv("PGP_KEY"), System.getenv("PGP_PASSWORD"))
    publishing.publications.withType<MavenPublication>().all { sign(this@all) }
}

val publishAllPublicationsToSonatype by tasks.registering {
    group = "publishing"
}

publishing.publications.withType<MavenPublication>().all {
    val capitalizedName = name.capitalize()
    println("Configuring publication \"$name\" for task \"publish${capitalizedName}PublicationToSonatype\".")
    publishing.repositories {
        maven {
            name = "Sonatype${capitalizedName}Intermediate"
            url = uri(layout.buildDirectory.dir("repository/${this@all.name}"))
        }
    }

    val sonatypeZip = tasks.register<Zip>("sonatypeZip${capitalizedName}") {
        dependsOn("publish${capitalizedName}PublicationToSonatype${capitalizedName}IntermediateRepository")
        from(layout.buildDirectory.dir("repository/${this@all.name}"))
        archiveBaseName.set("${project.name}-${this@all.name}")
        destinationDirectory.set(layout.buildDirectory.dir("publications/${this@all.name}"))
    }

    val publishTask = tasks.register<PublishSonatypeTask>("publish${capitalizedName}PublicationToSonatype") {
        group = "publishing"
        dependsOn(sonatypeZip)
        automatic = true
        publishAuthToken = System.getenv("SONATYPE_AUTH_TOKEN")
        zipFile = sonatypeZip.get().outputs.files.first()
    }

    publishAllPublicationsToSonatype.dependsOn(publishTask)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nassendelft/compose-win9x-theme")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("nassendelft")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}