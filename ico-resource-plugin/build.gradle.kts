plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("publishing-conventions")
}

group = "nl.ncaj.compose.plugin"

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
}

gradlePlugin {
    plugins {
        create("IcoResource") {
            id = "nl.ncaj.resource.ico"
            implementationClass = "nl.ncaj.compose.plugin.resource.ico.IcoResourcePlugin"
        }
    }
}

dependencies {
    implementation(libs.kotlinpoet)
    compileOnly(libs.kotlin.gradle.plugin)
}