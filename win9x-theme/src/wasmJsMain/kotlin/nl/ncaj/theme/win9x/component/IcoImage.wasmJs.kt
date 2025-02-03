package nl.ncaj.theme.win9x.component

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Document
import org.w3c.dom.HTMLCanvasElement
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private fun Uint8Array.copyInto(inputOffset: Int, output: ByteArray, outputOffset: Int, length: Int) {
    repeat(length) { index -> output[outputOffset + index] = this[inputOffset + index] }
}
private fun Uint8ClampedArray.copyInto(inputOffset: Int, output: ByteArray, outputOffset: Int, length: Int) {
    repeat(length) { index -> output[outputOffset + index] = this[inputOffset + index] }
}

private fun Uint8Array.toByteArray() = ByteArray(length).also { copyInto(0, it, 0, length) }
private fun Uint8ClampedArray.toByteArray() = ByteArray(length).also { copyInto(0, it, 0, length) }

// below set operator is used because the one in org.khronos.webgl.set seems to cut off the signing bit
// when using 'byte' values instead of Int
@Suppress("UNUSED_PARAMETER")
private fun setMethodImplForUint8ClampedArray(obj: Uint8ClampedArray, index: Int, value: Int) { js("obj[index] = value;") }
private operator fun Uint8ClampedArray.set(index: Int, value: Int) =
    setMethodImplForUint8ClampedArray(this, index, value)

@OptIn(ExperimentalStdlibApi::class)
private fun CanvasRenderingContext2D.createImageData(
    pixels: IntArray, // in ARGB format
    width: Int,
    height: Int,
) = createImageData(width.toDouble(), height.toDouble()).apply {
    for (i in 0 until (width * height)) {
        val argb = pixels[i]
        // data requires RGBA format
        data[(i * 4) + 0] = (argb shr 16) and 0xFF // Red
        data[(i * 4) + 1] = (argb shr 8) and 0xFF  // Green
        data[(i * 4) + 2] = argb and 0xFF          // Blue
        data[(i * 4) + 3] = (argb shr 24) and 0xFF // Alpha
    }
}

private fun Document.createHtmlCanvasElement(
    width: Int,
    height: Int
) = (createElement("canvas") as HTMLCanvasElement).apply {
    this.width = width
    this.height = height
}

private fun HTMLCanvasElement.putImageData(
    pixels: IntArray // in ARGB format
) = (getContext("2d") as CanvasRenderingContext2D).apply {
    putImageData(createImageData(pixels, width, height), 0.0, 0.0)
}

private enum class ImageType(val mimeType: String) { PNG("image/png"), JPEG("image/jpeg") }

private suspend fun HTMLCanvasElement.toImage(
    imageType: ImageType = ImageType.PNG,
) = suspendCoroutine { continuation ->
    toBlob({ blob ->
        FileReader().apply {
            onload = { continuation.resume(Uint8Array(result as ArrayBuffer).toByteArray()) }
            onerror = { continuation.resumeWithException(RuntimeException("Failed to read canvas data")) }
        }.readAsArrayBuffer(blob!!)
    }, imageType.mimeType)
}

internal actual suspend fun IntArray.encodeToPng(
    width: Int,
    height: Int
) = document.createHtmlCanvasElement(width, height)
    .apply { putImageData(this@encodeToPng) }
    .toImage()

@Composable
internal actual fun <T> rememberResourceState(
    key1: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    val scope = rememberCoroutineScope()
    return remember(key1) {
        val mutableState = mutableStateOf(getDefault())
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            mutableState.value = block()
        }
        mutableState
    }
}

@Composable
internal actual fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    val scope = rememberCoroutineScope()
    return remember(key1, key2) {
        val mutableState = mutableStateOf(getDefault())
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            mutableState.value = block()
        }
        mutableState
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
    val scope = rememberCoroutineScope()
    return remember(key1, key2, key3) {
        val mutableState = mutableStateOf(getDefault())
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            mutableState.value = block()
        }
        mutableState
    }
}