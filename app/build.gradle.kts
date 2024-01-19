plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.compose)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.activity.compose)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.jetbrains.skiko:skiko-awt-runtime-macos-arm64:0.7.89.1")
            }
        }
    }

    jvmToolchain(17)
}

android {
    namespace = "nl.ncaj.win9x"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets["main"].res.srcDirs("src/commonMain/resources")
}