package nl.ncaj.win9x.ui.theme


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo

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

internal fun Modifier.buttonNormalBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonHighlight,
        innerStartTop = Win98Theme.colorScheme.buttonFace,
        outerEndBottom = Win98Theme.colorScheme.buttonShadow,
        innerEndBottom = Win98Theme.colorScheme.windowFrame,
        borderWidth = Win98Theme.borderWidthPx
    )
}

internal fun Modifier.buttonPressedBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        innerStartTop = Win98Theme.colorScheme.windowFrame,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        innerEndBottom = Win98Theme.colorScheme.buttonFace,
        borderWidth = Win98Theme.borderWidthPx
    )
}

fun Modifier.statusBarBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        borderWidth = Win98Theme.borderWidthPx
    )
}

fun Modifier.win98Border(
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
) = this.drawWithCache {
    onDrawBehind {
        drawWin98Border(outerStartTop, innerStartTop, outerEndBottom, innerEndBottom, borderWidth)
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

class Win98Border(
    val outerStartTop: Color,
    val outerEndBottom: Color,
    val innerStartTop: Color? = null,
    val innerEndBottom: Color? = null,
    val borderWidth: Float
) {
    fun copy(
        outerStartTop: Color = this.outerStartTop,
        outerEndBottom: Color = this.outerEndBottom,
        innerStartTop: Color? = this.innerStartTop,
        innerEndBottom: Color? = this.innerEndBottom,
        borderWidth: Float = this.borderWidth
    ) = Win98Border(outerStartTop, outerEndBottom, innerStartTop, innerEndBottom, borderWidth)
}
