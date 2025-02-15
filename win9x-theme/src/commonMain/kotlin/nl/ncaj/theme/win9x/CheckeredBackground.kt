package nl.ncaj.theme.win9x


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.copy

fun Modifier.checkeredBackground(
    color1: Color,
    color2: Color,
    sizeBlock: Float = 1f
): Modifier = drawWithCache {
    val width = size.width
    val height = size.height

    val numTilesX = (width / (2 * sizeBlock)).toInt() + 1
    val numTilesY = (height / (2 * sizeBlock)).toInt() + 1

    val cachedBitmap = ImageBitmap(width.toInt(), height.toInt()).apply {
        val canvas = Canvas(this)
        val path = Path().apply {
            addRect(Rect(0f, 0f, sizeBlock, sizeBlock))
            addRect(Rect(sizeBlock, 0f, 2 * sizeBlock, sizeBlock))
            addRect(Rect(0f, sizeBlock, sizeBlock, 2 * sizeBlock))
            addRect(Rect(sizeBlock, sizeBlock, 2 * sizeBlock, 2 * sizeBlock))
        }

        for (i in 0 until numTilesX) {
            for (j in 0 until numTilesY) {
                val xOffset = i * 2 * sizeBlock
                val yOffset = j * 2 * sizeBlock

                val color = if ((i + j) % 2 == 0) color1 else color2

                val translatedPath = path.copy().apply { translate(Offset(xOffset, yOffset)) }

                canvas.drawPath(translatedPath, androidx.compose.ui.graphics.Paint().apply { this.color = color })
            }
        }
    }

    onDrawBehind {
        drawImage(cachedBitmap)
    }
}
