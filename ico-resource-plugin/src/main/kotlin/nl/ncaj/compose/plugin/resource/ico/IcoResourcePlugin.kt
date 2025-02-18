package nl.ncaj.compose.plugin.resource.ico

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


class IcoResourcePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val outputDir = project.layout.buildDirectory.dir("generated/compose/icoResourceGenerator/kotlin/commonMain")

        try {
            project.extensions.configure<KotlinMultiplatformExtension> {
                sourceSets.commonMain.configure {
                    kotlin.srcDir(outputDir)
                }
            }
        } catch (_: Exception) {
            project.logger.warn("Kotlin multiplatform plugin is not applied")
            return
        }

        val fileResourcesLocation = project.tasks
            .named("prepareComposeResourcesTaskForCommonMain")
            .map { it.outputs.files.singleFile }

        val generate = project.tasks.register<GenerateIcoResourcesTask>("generateIcoResources") {
            resourcesLocation.set(fileResourcesLocation)
            outputLocation.set(project.layout.buildDirectory.dir("generated/compose/icoResourceGenerator/kotlin/commonMain"))
        }

        project.tasks.named("generateResourceAccessorsForCommonMain").configure {
            finalizedBy(generate)
        }

        project.tasks.withType<KotlinCompile>().configureEach {
            dependsOn(generate)
        }

        project.tasks.withType<Kotlin2JsCompile>().configureEach {
            dependsOn(generate)
        }
    }
}

