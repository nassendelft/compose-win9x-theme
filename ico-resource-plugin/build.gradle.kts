plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

group = "nl.ncaj"
version = "unspecified"

kotlin {
    jvmToolchain(17)
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
    implementation("com.squareup:kotlinpoet:2.0.0")
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}