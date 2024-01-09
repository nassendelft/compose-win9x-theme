package nl.ncaj.win9x.ui.theme


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo


fun Modifier.checkeredBackground(
    color1: Color = Color.Unspecified,
    color2: Color = Color.Unspecified,
    size: Float = 5f
): Modifier = composed {
    then(
        CheckeredBackgroundElement(
            if (color1 == Color.Unspecified) Win98Theme.colorScheme.buttonFace else color1,
            if (color2 == Color.Unspecified) Win98Theme.colorScheme.buttonHighlight else color2,
            size,
            inspectorInfo = {
                debugInspectorInfo {
                    name = "CheckeredBackground"
                    properties["color1"] = color1
                    properties["color2"] = color2
                    properties["size"] = size
                }
            }
        )
    )
}

private class CheckeredBackgroundElement(
    private val color1: Color,
    private val color2: Color,
    private val size: Float,
    private val inspectorInfo: InspectorInfo.() -> Unit
) : ModifierNodeElement<CheckeredBackgroundNode>() {
    override fun create() = CheckeredBackgroundNode(color1, color2, size)

    override fun update(node: CheckeredBackgroundNode) {
        node.color1 = color1
        node.color2 = color2
        node.sizeBlock = size
    }

    override fun InspectorInfo.inspectableProperties() = inspectorInfo()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CheckeredBackgroundElement

        if (color1 != other.color1) return false
        if (color2 != other.color2) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color1.hashCode()
        result = 31 * result + color2.hashCode()
        result = 31 * result + size.hashCode()
        return result
    }
}

private class CheckeredBackgroundNode(
    var color1: Color,
    var color2: Color,
    var sizeBlock: Float
) : DrawModifierNode, Modifier.Node() {

    override fun ContentDrawScope.draw() {
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
        drawContent()
    }
}
