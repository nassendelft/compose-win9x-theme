import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import java.net.HttpURLConnection
import java.net.URL

abstract class PublishSonatypeTask: DefaultTask() {

    @get:Input
    abstract val publishAuthToken: Property<String>

    @get:Input
    abstract val automatic: Property<Boolean>

    @get:InputFiles
    abstract val zipFile: RegularFileProperty

    @TaskAction
    fun execute() {
        val boundary = "boundary"
        val automatic = automatic.getOrElse(false)
        val url = URL("https://central.sonatype.com/api/v1/publisher/upload" + if(automatic) "?publishingType=AUTOMATIC" else "")
        val file = zipFile.get().asFile
        (url.openConnection() as HttpURLConnection).apply {
            doOutput = true

            requestMethod = "POST"
            setRequestProperty("Authorization", "Bearer ${publishAuthToken.get()}")
            setRequestProperty("Content-Type", "multipart/form-data; boundary=${boundary}")
            outputStream.use {
                it.write("--$boundary\r\n".toByteArray())
                it.write("--$boundary\r\n".toByteArray())
                it.write("Content-Disposition: form-data; name=\"bundle\"; filename=\"${file.name}\"\r\n".toByteArray())
                it.write("Content-Type: application/octet-stream\r\n".toByteArray())
                it.write("\r\n".toByteArray())
                file.inputStream().copyTo(it)
                it.write("\r\n".toByteArray())
                it.write("--$boundary".toByteArray())
            }

            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                println(inputStream.use { it.readAllBytes() }.decodeToString())
            } else {
                error("Publishing to Sonatype failed with response code $responseCode")
            }
        }
    }
}