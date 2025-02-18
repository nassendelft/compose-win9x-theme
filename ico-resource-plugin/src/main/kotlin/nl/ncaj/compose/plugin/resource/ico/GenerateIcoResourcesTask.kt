package nl.ncaj.compose.plugin.resource.ico

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateIcoResourcesTask: DefaultTask() {

    @get:InputDirectory
    abstract val resourcesLocation: Property<File>

    @get:OutputDirectory
    abstract val outputLocation: DirectoryProperty

    @TaskAction
    fun execute() {
        val projectName = project.name.replace(' ', '_').replace('-', '_')
        val packageName = "${project.group}.$projectName.generated.resources"
        val icoResourcesClass = ClassName(packageName, "IcoResources")
        val icoResourceClass = ClassName("nl.ncaj.compose.resource.ico", "IcoResource")

        val icoResourcesBuilder = TypeSpec.Companion.objectBuilder(icoResourcesClass)
            .addModifiers(KModifier.INTERNAL)

        File(resourcesLocation.get(), "files")
            .listFiles()
            ?.filter { it.extension.equals("ico", ignoreCase = true) }
            ?.forEach { file ->
                val filename = file.name
                val resourcePath = "composeResources/$packageName/files/$filename"
                val propertyName = file.nameWithoutExtension.replace(' ', '_').replace('-', '_')
                icoResourcesBuilder.addProperty(
                    PropertySpec.Companion.builder(propertyName, icoResourceClass)
                        .delegate("""lazy { IcoResource("ico:${filename}", "$resourcePath") }""")
                        .build()
                )
            }

        val icoResourcesTypeSpec = icoResourcesBuilder.build()

        val fileSpec = FileSpec.Companion.builder(packageName, "Ico")
            .addType(icoResourcesTypeSpec)
            .addProperty(
                PropertySpec.Companion.builder("ico", icoResourcesClass, KModifier.INTERNAL)
                    .receiver(ClassName(packageName, "Res"))
                    .getter(
                        FunSpec.Companion.getterBuilder()
                            .addCode("return %T", icoResourcesClass)
                            .build()
                    )
                    .build()
            )
            .build()

        fileSpec.writeTo(outputLocation.get().asFile)
    }
}