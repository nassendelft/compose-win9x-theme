import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
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
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":win9x-theme"))
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.components.resources)
        }
        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(compose.preview)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.8.15")
            implementation("androidx.collection:collection:1.4.5")
            implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.7")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
        }
    }

    jvmToolchain(17)
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

android {
    namespace = "nl.ncaj.win9x.example"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}