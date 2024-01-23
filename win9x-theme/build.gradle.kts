import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.compose)
}

kotlin {
    androidTarget()
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
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.common)
        }
    }

    jvmToolchain(17)

    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.experimental {
    web.application {}
}

android {
    namespace = "nl.ncaj.win9x"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
}