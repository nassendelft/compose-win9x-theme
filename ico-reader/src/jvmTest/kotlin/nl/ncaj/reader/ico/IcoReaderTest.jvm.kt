package nl.ncaj.reader.ico

import kotlinx.io.bytestring.ByteString

actual fun readResource(resourceName: String) = ByteString(
    ClassLoader.getSystemResourceAsStream(resourceName)!!.readBytes()
)