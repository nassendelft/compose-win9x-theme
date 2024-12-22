package nl.ncaj.theme.win9x.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

internal actual suspend fun IntArray.encodeToPng(width: Int, height: Int): ByteArray {
    val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    for (y in 0..<height) {
        for (x in 0..<width) {
            val pixelIndex: Int = y * width + x
            img.setRGB(x, y, this[pixelIndex])
        }
    }
    return ByteArrayOutputStream().use { output ->
        check(ImageIO.write(img, "png", output))
        output.toByteArray()
    }
}

@Composable
internal actual fun <T> rememberResourceState(
    key1: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    return remember(key1) {
        mutableStateOf(runBlocking { block() })
    }
}

@Composable
internal actual fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    return remember(key1, key2) {
        mutableStateOf(runBlocking { block() })
    }
}

@Composable
internal actual fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    key3: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    return remember(key1, key2, key3) {
        mutableStateOf(runBlocking { block() })
    }
}