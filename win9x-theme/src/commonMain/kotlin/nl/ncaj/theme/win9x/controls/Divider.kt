package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import nl.ncaj.theme.win9x.Win9xTheme

@Composable
fun Divider(
    modifier: Modifier = Modifier
) {
    val topLineColor = Win9xTheme.colorScheme.buttonShadow
    val bottomLineColor = Win9xTheme.colorScheme.buttonHighlight

    Canvas(
        modifier = modifier
            .defaultMinSize(minHeight = Win9xTheme.borderWidthDp)
    ) {
        drawLine(
            color = topLineColor,
            start = Offset(0f, size.height / 4),
            end = Offset(size.width, size.height / 4),
            strokeWidth = size.height / 2
        )
        drawLine(
            color = bottomLineColor,
            start = Offset(0f, size.height - (size.height / 4)),
            end = Offset(size.width, size.height - (size.height / 4)),
            strokeWidth = size.height / 2
        )
    }
}