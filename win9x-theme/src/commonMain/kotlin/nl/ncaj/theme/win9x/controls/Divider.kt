package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import nl.ncaj.theme.win9x.Win9xTheme

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier
) {
    val topLineColor = Win9xTheme.colorScheme.buttonShadow
    val bottomLineColor = Win9xTheme.colorScheme.buttonHighlight

    Box(modifier.defaultMinSize(minHeight = Win9xTheme.borderWidthDp).drawBehind {
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
    })
}