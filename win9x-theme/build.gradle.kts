import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrainsCompose)
    `maven-publish`
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
    repositories {
        maven {
            name = "GitHubPackages"
            setUrl("https://maven.pkg.github.com/nassendelft/compose-win9x-theme")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
