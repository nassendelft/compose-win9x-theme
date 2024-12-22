package nl.ncaj.theme.win9x.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap

internal expect suspend fun IntArray.encodeToPng(width: Int, height: Int): ByteArray

private val ImageBitmap.size get() = IntSize(width, height)

private fun IntSize.isContainedIn(other: IntSize) = width <= other.width && height <= other.height
private val IntSize.Companion.areaComparator get() = { a: IcoResource, b: IcoResource ->
    a.image.width * a.image.height - b.image.width * b.image.height
}

@Composable
internal expect fun <T> rememberResourceState(
    key1: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T>

@Composable
internal expect fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T>

@Composable
internal expect fun <T> rememberResourceState(
    key1: Any,
    key2: Any,
    key3: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T>

class IcoResource(
    val image: ImageBitmap,
    val colorCount: Int
)

private val emptyImageBitmap: ImageBitmap by lazy { ImageBitmap(1, 1) }

@OptIn(ExperimentalResourceApi::class, ExperimentalStdlibApi::class)
@Composable
fun IcoImage(
    data: suspend () -> ByteArray,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = alignment
    ) {
        val painter by rememberResourceState(data, constraints, { BitmapPainter(emptyImageBitmap) }) {
            IcoReader.read(data())
                .images
                .map { IcoResource(it.data.encodeToPng(it.width, it.height).decodeToImageBitmap(), it.colorCount) }
                .findFittingImage(Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat()))
                .let { BitmapPainter(it?.image ?: emptyImageBitmap) }
        }
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.None,
        )
    }
}

private fun List<IcoResource>.findFittingImage(size: Size): IcoResource? {
    // TODO probably better to use LocalComposition to select a color count
    // for now though, we just select the highest number
    val bpp = maxOf { it.colorCount }
    return filter { it.colorCount == bpp }
        .filter { it.image.size.isContainedIn(size.toIntSize()) }
        .takeIf { it.isNotEmpty() }
        ?.maxOfWith(IntSize.areaComparator) { it }
}
