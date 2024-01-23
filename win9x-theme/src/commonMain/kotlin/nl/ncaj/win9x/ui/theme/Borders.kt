package nl.ncaj.win9x.ui.theme


import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

fun Modifier.groupingBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
        innerStartTop = Win9xTheme.colorScheme.buttonHighlight,
        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
        innerEndBottom = Win9xTheme.colorScheme.buttonShadow,
        borderWidth = Win9xTheme.borderWidthPx,
    )
}

fun Modifier.windowBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonFace,
        innerStartTop = Win9xTheme.colorScheme.buttonHighlight,
        outerEndBottom = Win9xTheme.colorScheme.windowFrame,
        innerEndBottom = Win9xTheme.colorScheme.buttonShadow,
        borderWidth = Win9xTheme.borderWidthPx,
    )
}

fun Modifier.sunkenBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
        innerStartTop = Win9xTheme.colorScheme.windowFrame,
        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
        innerEndBottom = Win9xTheme.colorScheme.buttonFace,
        borderWidth = Win9xTheme.borderWidthPx
    )
}

internal fun Modifier.buttonNormalBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonHighlight,
        innerStartTop = Win9xTheme.colorScheme.buttonFace,
        outerEndBottom = Win9xTheme.colorScheme.buttonShadow,
        innerEndBottom = Win9xTheme.colorScheme.windowFrame,
        borderWidth = Win9xTheme.borderWidthPx
    )
}

internal fun Modifier.buttonPressedBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
        innerStartTop = Win9xTheme.colorScheme.windowFrame,
        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
        innerEndBottom = Win9xTheme.colorScheme.buttonFace,
        borderWidth = Win9xTheme.borderWidthPx
    )
}

fun Modifier.statusBarBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
        borderWidth = Win9xTheme.borderWidthPx
    )
}

fun Modifier.win9xBorder(
    win9xBorder: Win9xBorder
) = this.win9xBorder(
    outerStartTop = win9xBorder.outerStartTop,
    innerStartTop = win9xBorder.innerStartTop,
    outerEndBottom = win9xBorder.outerEndBottom,
    innerEndBottom = win9xBorder.innerEndBottom,
    borderWidth = win9xBorder.borderWidth
)

internal fun Modifier.win9xBorder(
    outerStartTop: Color,
    outerEndBottom: Color,
    innerStartTop: Color? = null,
    innerEndBottom: Color? = null,
    borderWidth: Float,
) = this.drawWithCache {
    onDrawBehind {
        drawWin9xBorder(outerStartTop, innerStartTop, outerEndBottom, innerEndBottom, borderWidth)
    }
}

internal fun DrawScope.drawWin9xBorder(
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

class Win9xBorder(
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
    ) = Win9xBorder(outerStartTop, outerEndBottom, innerStartTop, innerEndBottom, borderWidth)
}
