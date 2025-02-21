plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlin.multiplatform.plugin)
    implementation(libs.android.gradle.plugin)
}