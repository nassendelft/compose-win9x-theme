package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.win9xBorder

@Composable
fun ProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = Win9xTheme.colorScheme.selection
) {
    Box(
        modifier
            .defaultMinSize(100.dp, 20.dp)
            .background(Win9xTheme.colorScheme.buttonFace)
            .progressIndicatorBorder()
            .padding(Win9xTheme.borderWidthDp + 1.dp)
            .progressIndicator(progress, color)
    )
}

internal fun Modifier.progressIndicatorBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
        borderWidth = Win9xTheme.borderWidthPx,
    )
}

private fun Modifier.progressIndicator(
    progress: Float,
    color: Color
) = this.drawBehind {
    drawLine(
        color = color,
        start = Offset(0f, size.height / 2),
        end = Offset(size.width * progress, size.height / 2),
        strokeWidth = size.height,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 4f), 1f)
    )
}
