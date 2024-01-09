package nl.ncaj.win9x.ui.theme


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import nl.ncaj.win9x.ui.theme.controls.Win98Border

internal fun Modifier.groupingBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        innerStartTop = Win98Theme.colorScheme.buttonHighlight,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        innerEndBottom = Win98Theme.colorScheme.buttonShadow,
        borderWidth = Win98Theme.borderWidthPx,
    )
}

internal fun Modifier.windowBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonFace,
        innerStartTop = Win98Theme.colorScheme.buttonHighlight,
        outerEndBottom = Win98Theme.colorScheme.windowFrame,
        innerEndBottom = Win98Theme.colorScheme.buttonShadow,
        borderWidth = Win98Theme.borderWidthPx,
    )
}

internal fun Modifier.sunkenBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        innerStartTop = Win98Theme.colorScheme.windowFrame,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        innerEndBottom = Win98Theme.colorScheme.buttonFace,
        borderWidth = Win98Theme.borderWidthPx
    )
}

internal fun Modifier.win98Border(
    win98Border: Win98Border
) = this.win98Border(
    outerStartTop = win98Border.outerStartTop,
    innerStartTop = win98Border.innerStartTop,
    outerEndBottom = win98Border.outerEndBottom,
    innerEndBottom = win98Border.innerEndBottom,
    borderWidth = win98Border.borderWidth
)

internal fun Modifier.win98Border(
    outerStartTop: Color,
    outerEndBottom: Color,
    innerStartTop: Color? = null,
    innerEndBottom: Color? = null,
    borderWidth: Float,
): Modifier = this.then(
    BorderElement(
        outerStartTop,
        innerStartTop,
        outerEndBottom,
        innerEndBottom,
        borderWidth,
        inspectorInfo = {
            debugInspectorInfo {
                name = "win98Border"
                properties["topLeftOuter"] = outerStartTop
                properties["topLeftInner"] = innerStartTop
                properties["bottomRightOuter"] = outerEndBottom
                properties["bottomRightInner"] = innerEndBottom
                properties["borderWidth"] = borderWidth
            }
        }
    )
)

private class BorderElement(
    private val topLeftOuter: Color,
    private val topLeftInner: Color?,
    private val bottomRightOuter: Color,
    private val bottomRightInner: Color?,
    private val borderWidth: Float,
    private val inspectorInfo: InspectorInfo.() -> Unit
) : ModifierNodeElement<BorderNode>() {
    override fun create() = BorderNode(
        topLeftOuter,
        topLeftInner,
        bottomRightOuter,
        bottomRightInner,
        borderWidth
    )

    override fun update(node: BorderNode) {
        node.topLeftOuter = topLeftOuter
        node.topLeftInner = topLeftInner
        node.bottomRightOuter = bottomRightOuter
        node.bottomRightInner = bottomRightInner
        node.borderWidth = borderWidth
    }

    override fun InspectorInfo.inspectableProperties() = inspectorInfo()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BorderElement

        if (topLeftOuter != other.topLeftOuter) return false
        if (topLeftInner != other.topLeftInner) return false
        if (bottomRightOuter != other.bottomRightOuter) return false
        if (bottomRightInner != other.bottomRightInner) return false
        if (borderWidth != other.borderWidth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeftOuter.hashCode()
        result = 31 * result + topLeftInner.hashCode()
        result = 31 * result + bottomRightOuter.hashCode()
        result = 31 * result + bottomRightInner.hashCode()
        result = 31 * result + borderWidth.hashCode()
        return result
    }
}

private class BorderNode(
    var topLeftOuter: Color,
    var topLeftInner: Color?,
    var bottomRightOuter: Color,
    var bottomRightInner: Color?,
    var borderWidth: Float,
) : DrawModifierNode, Modifier.Node() {

    override fun ContentDrawScope.draw() {
        drawWin98Border(topLeftOuter, topLeftInner, bottomRightOuter, bottomRightInner, borderWidth)
        drawContent()
    }
}

internal fun DrawScope.drawWin98Border(
    outerStartTop: Color,
    innerStartTop: Color?,
    outerEndBottom: Color,
    innerEndBottom: Color?,
    borderWidth: Float,
) {
    val strokeWidth = borderWidth / 2
    val halfStrokeWidth = strokeWidth / 2
    drawLine(
        color = outerStartTop,
        start = Offset(halfStrokeWidth, 0f),
        end = Offset(halfStrokeWidth, size.height - strokeWidth),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = outerStartTop,
        start = Offset(strokeWidth, halfStrokeWidth),
        end = Offset(size.width - strokeWidth, halfStrokeWidth),
        strokeWidth = strokeWidth
    )
    innerStartTop?.let {
        drawLine(
            color = innerStartTop,
            start = Offset(strokeWidth + halfStrokeWidth, strokeWidth),
            end = Offset(strokeWidth + halfStrokeWidth, size.height - strokeWidth - strokeWidth),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = innerStartTop,
            start = Offset(
                x = strokeWidth + halfStrokeWidth + halfStrokeWidth,
                y = strokeWidth + halfStrokeWidth
            ),
            end = Offset(size.width - (strokeWidth * 2), strokeWidth + halfStrokeWidth),
            strokeWidth = strokeWidth
        )
    }
    drawLine(
        color = outerEndBottom,
        start = Offset(0f, size.height - halfStrokeWidth),
        end = Offset(size.width, size.height - halfStrokeWidth),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = outerEndBottom,
        start = Offset(size.width - halfStrokeWidth, size.height),
        end = Offset(size.width - halfStrokeWidth, 0f),
        strokeWidth = strokeWidth
    )
    innerEndBottom?.let {
        drawLine(
            color = innerEndBottom,
            start = Offset(strokeWidth, size.height - strokeWidth - halfStrokeWidth),
            end = Offset(size.width - strokeWidth, size.height - strokeWidth - halfStrokeWidth),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = innerEndBottom,
            start = Offset(size.width - strokeWidth - halfStrokeWidth, strokeWidth),
            end = Offset(size.width - strokeWidth - halfStrokeWidth, size.height - strokeWidth),
            strokeWidth = strokeWidth
        )
    }
}
