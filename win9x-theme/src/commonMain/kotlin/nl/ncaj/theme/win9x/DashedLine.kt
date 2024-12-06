package nl.ncaj.theme.win9x

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalDensity

private val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)

@Composable
internal fun DashedVerticalLine(
    modifier: Modifier = Modifier,
) = with(LocalDensity.current) {
    val dashColor = Win9xTheme.colorScheme.buttonShadow
    Canvas(modifier.defaultMinSize(minWidth = 2.toDp())) {
        drawLine(
            color = dashColor,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            pathEffect = dashPathEffect
        )
    }
}

@Composable
internal fun DashedHorizontalLine(
    modifier: Modifier = Modifier,
) = with(LocalDensity.current) {
    val dashColor = Win9xTheme.colorScheme.buttonShadow
    Canvas(modifier.defaultMinSize(minHeight = 2.toDp())) {
        drawLine(
            color = dashColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            pathEffect = dashPathEffect
        )
    }
}
