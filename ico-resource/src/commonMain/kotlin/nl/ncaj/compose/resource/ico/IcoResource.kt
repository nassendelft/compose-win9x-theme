package nl.ncaj.compose.resource.ico

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toIntSize
import org.jetbrains.compose.resources.*

/**
 * Represents a ico resource.
 *
 * @param id The identifier of the ico resource.
 * @param path The path of resource associated with the ico resource.
 *
 * @see Resource
 */
@Immutable
class IcoResource(val id: String, val path: String)

/**
 * Creates an ico using the specified ico resource.
 *
 * @param resource The font resource to be used.
 *
 * @return The created [Ico] object.
 *
 * @throws MissingResourceException if the specified resource ID is not found.
 */
@OptIn(InternalResourceApi::class)
@Composable
fun Ico(resource: IcoResource): Ico {
    val ico by rememberResourceState(resource, { emptyIco }) {
        IcoReader.read(readResourceBytes(resource.path))
    }
    return ico
}

internal expect suspend fun IntArray.encodeToPng(width: Int, height: Int): ByteArray

private val ImageBitmap.size get() = IntSize(width, height)

private fun IntSize.isContainedIn(other: IntSize) = width <= other.width && height <= other.height
private val IntSize.Companion.areaComparator
    get() = { a: IcoBitmap, b: IcoBitmap -> a.image.width * a.image.height - b.image.width * b.image.height }

@Composable
internal expect fun <T: Any> rememberResourceState(
    key1: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T>

internal val emptyImageBitmap: ImageBitmap by lazy { ImageBitmap(1, 1) }

@Composable
fun painterResource(
    resource: IcoResource,
    size: Size
): BitmapPainter {
    val res = icoBitmapResource(resource)
    val painter = remember(res, size) { BitmapPainter(res.findFittingImage(size)?.image ?: emptyImageBitmap) }
    return painter
}

@OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)
@Composable
fun icoBitmapResource(resource: IcoResource): List<IcoBitmap> {
    val bitmaps by rememberResourceState(resource, { emptyList<IcoBitmap>() }) {
        IcoReader.read(readResourceBytes(resource.path))
            .images.map { IcoBitmap(it.data.encodeToPng(it.width, it.height).decodeToImageBitmap(), it.colorCount) }
    }
    return bitmaps
}

class IcoBitmap internal constructor(
    val image: ImageBitmap,
    val colorCount: Int
)

internal fun List<IcoBitmap>.findFittingImage(size: Size): IcoBitmap? {
    if (isEmpty()) return null
    // TODO probably better to use LocalComposition to select a color count
    // for now though, we just select the highest number
    val bpp = maxOf { it.colorCount }
    return filter { it.colorCount == bpp }
        .filter { it.image.size.isContainedIn(size.toIntSize()) }
        .takeIf { it.isNotEmpty() }
        ?.maxOfWith(IntSize.areaComparator) { it }
}