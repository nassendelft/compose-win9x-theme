package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import nl.ncaj.win9x.ui.theme.Win98Theme

@Composable
fun Divider(
    modifier: Modifier = Modifier
) {
    val topLineColor = Win98Theme.colorScheme.buttonShadow
    val bottomLineColor = Win98Theme.colorScheme.buttonHighlight

    Canvas(
        modifier = modifier
            .defaultMinSize(minHeight = Win98Theme.borderWidthDp)
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