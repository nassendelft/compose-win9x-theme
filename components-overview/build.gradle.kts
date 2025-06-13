import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.application")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    id("nl.ncaj.resource.ico")
}

group = "nl.ncaj.theme.win9x"

kotlin {
    jvm()
    androidTarget()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "win9xComponentsOverview.js"
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":win9x-theme"))
            implementation(project(":ico-resource"))
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
            implementation("androidx.collection:collection:1.5.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel:2.9.1")
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
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