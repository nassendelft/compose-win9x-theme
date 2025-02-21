plugins {
    id("com.android.application") version libs.versions.agp.get() apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
}
