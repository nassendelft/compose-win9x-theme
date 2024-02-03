package nl.ncaj.theme.win9x


import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

fun Modifier.checkeredBackground(
    color1: Color,
    color2: Color,
    sizeBlock: Float = 5f
): Modifier = drawWithCache {
    onDrawBehind {
        val columns = (size.width / sizeBlock).toInt()
        val rows = (size.height / sizeBlock).toInt()

        val remainingSpaceY = size.height % sizeBlock
        val remainingSpaceX = size.width % sizeBlock
        var currentTileSizeY: Float
        var currentTileSizeX: Float

        for (row in 0..rows) {
            for (column in 0..columns) {
                currentTileSizeY = if (row == rows && sizeBlock * (row + 1) > size.height) {
                    remainingSpaceY
                } else {
                    sizeBlock
                }
                currentTileSizeX = if (column == columns && sizeBlock * (column + 1) > size.width) {
                    remainingSpaceX
                } else {
                    sizeBlock
                }
                drawRect(
                    color = if ((column + row) % 2 == 0) color1 else color2,
                    topLeft = Offset(column * sizeBlock, row * sizeBlock),
                    size = Size(currentTileSizeX, currentTileSizeY),
                )
            }
        }
    }
}
