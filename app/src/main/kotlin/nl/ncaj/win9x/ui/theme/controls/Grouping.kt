package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.groupingBorder

@Composable
fun GroupingPreview() {
    Column {
        Text("- Grouping -")
        Spacer(modifier = Modifier.height(2.dp))

        Grouping("Group") {
            Spacer(Modifier.size(90.dp, height = 20.dp))
        }
    }
}

@Composable
fun Grouping(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var textSize by remember { mutableStateOf(IntSize.Zero) }

    val xOffsetPx = with(LocalDensity.current) { 6.dp.toPx() }
    val yOffsetDp = with(LocalDensity.current) {
        (if (textSize == IntSize.Zero || textSize.height == 0) 0 else textSize.height / 2).toDp()
    }

    val borderWidth = Win98Theme.borderWidthPx

    val shape = GenericShape { size, _ ->
        lineTo(xOffsetPx, 0f)
        relativeLineTo(0f, borderWidth * 2)
        relativeLineTo(textSize.width.toFloat() + xOffsetPx, 0f)
        relativeLineTo(0f, -borderWidth * 2)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
    }

    Box {
        Box(
            modifier = Modifier
                .padding(top = yOffsetDp)
                .matchParentSize()
                .clip(shape)
                .groupingBorder()
        )
        Text(
            text = label,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .onSizeChanged { textSize = it },
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = modifier.padding(
                start = 8.dp,
                top = yOffsetDp + 8.dp,
                end = 8.dp,
                bottom = 8.dp
            ),
            content = content
        )
    }
}