import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose)
}

kotlin {
    jvm()
    androidTarget()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "win9xComponentsOverview.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.rootDir.path)
                        // Includes resources from lib module
                        add("${project.project(":win9x-theme").projectDir.path}/src/commonMain/resources")
                    }
                }
            }

            // applyBinaryen()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(project(":win9x-theme"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(compose.preview)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.89.1")
        }
    }

    jvmToolchain(17)
}

compose.experimental {
    web.application {}
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

android {
    namespace = "nl.ncaj.win9x.example"
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