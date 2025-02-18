package nl.ncaj.compose.resource.ico

import kotlinx.io.*
import kotlin.math.ceil

class Ico internal constructor(
    val type: IconType,
    val images: List<IcoImage>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Ico

        if (type != other.type) return false
        if (images != other.images) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + images.hashCode()
        return result
    }
}

class IcoImage internal constructor(
    val data: IntArray,
    val width: Int,
    val height: Int,
    val colorCount: Int,
)

internal val emptyIco = Ico(IconType.ICON, emptyList())

// https://en.wikipedia.org/wiki/ICO_(file_format)
// https://en.wikipedia.org/wiki/BMP_file_format
// https://devblogs.microsoft.com/oldnewthing/20101018-00/?p=12513
object IcoReader {

    fun read(file: ByteArray): Ico {
        val headerSource = Buffer().also { it.write(file, 2, 6) } as Source
        val iconType = IconType.fromIndex(headerSource.readShortLe())
        val imageCount = headerSource.readShortLe()

        val imageDirectorySource = Buffer().also { it.write(file, 6, 6 + (16 * imageCount)) } as Source
        val images = imageDirectorySource.readEntries(iconType, imageCount)
            .map { entry ->
                Buffer().also { it.write(file, entry.offset, entry.offset + entry.size) }.readImage(entry)
            }
        return Ico(iconType, images)
    }

    private fun Source.readImage(entry: IconDirectoryEntry): IcoImage {
        check(entry is IconDirectoryEntry.Icon) { "Cursor entry is not supported" }

        // BitmapInfoHeader data
        check(readIntLe() == 40) { "Not a valid BitmapInfoHeader, PNG is not supported" }
        val width = readIntLe() // Width of the bitmap in pixels
        val height = readIntLe() // Height of the bitmap in pixels
        val planes = readShortLe() // Number of color planes (must be 1)
        val bitCount = readShortLe().toInt() // Bits per pixel
        val compression = readIntLe() // Type of compression
        val sizeImage = readIntLe() // Size of image data in bytes, can be 0 for no compression
        val xPixelsPerMeter = readIntLe() // Horizontal resolution in pixels per meter
        val yPixelsPerMeter = readIntLe() // Vertical resolution in pixels per meter
        val colorUsed = readIntLe() // Number of colors used in the bitmap
            .takeIf { it != 0 }
            ?: (1 shl bitCount) // fallback to 2^bitCount
        val colorImportant = readIntLe() // Number of colors that are important

        check(width > 0) { "Bitmap width must be greater than zero" }
        check(height != 0) { "Bitmap height must not be zero" }
        check(planes == 1.toShort()) { "Color planes must be 1" }
        check(compression == 0x00) { "Compression must be 0" }
        check(listOf(1, 4, 8, 16, 24, 32).contains(bitCount)) { "Bit count must be one of 1, 4, 8, 16, 24, 32" }
        check(bitCount < 24) { "Only 1, 4, 8 and 16 bits images are supported" }

        val image = IntArray(entry.width * entry.height)

        // color palette data
        val colorPalette = (0 until colorUsed).map { readIntLe() or (0xFF shl 24) }

        // pixel data
        val pixelsPerByte = 8 / entry.bitsPerPixel
        for (y in entry.height - 1 downTo 0) {
            val byteCount = (ceil(entry.width * entry.bitsPerPixel / 32.0) * 4).toInt()
            val row = readByteString(byteCount)
            for (x in 0 until entry.width) {
                val byteIndex = x / pixelsPerByte
                val shift = (pixelsPerByte - 1 - x % pixelsPerByte) * entry.bitsPerPixel
                val colorIndex = row[byteIndex].toInt() ushr shift and (1 shl entry.bitsPerPixel) - 1
                image[y * entry.width + x] = colorPalette[colorIndex]
            }
        }

        // mask data
        for (y in entry.height - 1 downTo 0) {
            val byteCount = (ceil(entry.width / 32.0) * 4).toInt()
            val row = readByteString(byteCount)
            for (x in 0 until entry.width) {
                val pixelIndex = y * entry.width + x
                val maskByte = row[x / 8].toInt()
                val maskBitIndex = 8 - (x % 8)
                if (maskByte and (1 shl maskBitIndex) != 0) {
                    // set alpha to 0 (transparent)
                    image[pixelIndex] = image[pixelIndex] and 0x00FFFFFF
                }
            }
        }

        return IcoImage(image, entry.width, entry.height, colorUsed)
    }

    private fun Source.readEntries(
        iconType: IconType,
        images: Short
    ): List<IconDirectoryEntry> {
        return when (iconType) {
            IconType.ICON -> (0 until images).map {
                IconDirectoryEntry.Icon(
                    width = readByte().toInt().takeIf { it != 0 } ?: 256,
                    height = readByte().toInt().takeIf { it != 0 } ?: 256,
                    colors = readByte().toInt().also { readByte() /* unused */ },
                    colorPlanes = readShortLe().toInt(),
                    bitsPerPixel = readShortLe().toInt(),
                    size = readIntLe(),
                    offset = readIntLe()
                )
            }

            IconType.CURSOR -> (0 until images).map {
                IconDirectoryEntry.Cursor(
                    width = readByte().toInt().takeIf { it != 0 } ?: 256,
                    height = readByte().toInt().takeIf { it != 0 } ?: 256,
                    colors = readByte().toInt().also { readByte() /* unused */ },
                    horizontalPixelOffset = readShortLe().toInt(),
                    verticalPixelOffset = readShortLe().toInt(),
                    size = readIntLe(),
                    offset = readIntLe()
                )
            }
        }
    }
}


enum class IconType {
    ICON, CURSOR;

    internal companion object {
        fun fromIndex(index: Short): IconType = when (index) {
            1.toShort() -> ICON
            2.toShort() -> CURSOR
            else -> error("Unknown icon type: $index")
        }
    }
}

private sealed class IconDirectoryEntry(
    val width: Int,
    val height: Int,
    val colors: Int,
    val size: Int,
    val offset: Int,
) {
    // colorPlanes: https://en.wikipedia.org/wiki/ICO_(file_format)#cite_note-iconPlanes-15
    // bitPerPixel: https://en.wikipedia.org/wiki/ICO_(file_format)#cite_note-iconBpp-17
    class Icon(
        width: Int,
        height: Int,
        colors: Int,
        size: Int,
        offset: Int,
        val colorPlanes: Int,
        val bitsPerPixel: Int,
    ) : IconDirectoryEntry(width, height, colors, size, offset) {
        override fun toString() =
            "Icon(colorPlanes=$colorPlanes, bitPerPixel=$bitsPerPixel) ${super.toString()}"
    }

    // offsets: https://en.wikipedia.org/wiki/ICO_(file_format)#cite_note-cursorBpp-16
    class Cursor(
        width: Int,
        height: Int,
        colors: Int,
        size: Int,
        offset: Int,
        val horizontalPixelOffset: Int,
        val verticalPixelOffset: Int,
    ) : IconDirectoryEntry(width, height, colors, size, offset)

    override fun toString() =
        "IconDirectoryEntry(width=$width, height=$height, colors=$colors, size=$size, offset=$offset)"
}
