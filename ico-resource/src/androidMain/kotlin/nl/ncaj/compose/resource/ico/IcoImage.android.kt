package nl.ncaj.compose.resource.ico

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

internal actual suspend fun IntArray.encodeToPng(width: Int, height: Int): ByteArray {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        .apply { setPixels(this@encodeToPng, 0, width, 0, 0, width, height) }
    return ByteArrayOutputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        out.toByteArray()
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