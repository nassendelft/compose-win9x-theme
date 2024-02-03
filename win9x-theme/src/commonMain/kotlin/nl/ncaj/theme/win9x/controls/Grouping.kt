package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.groupingBorder
import kotlin.math.max


@Composable
fun Grouping(
    label: String,
    modifier: Modifier = Modifier,
    labelAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable () -> Unit
) = with(LocalDensity.current) {
    var textSize by remember { mutableStateOf(IntSize.Zero) }
    var availableLabelSpace by remember { mutableIntStateOf(0) }

    val textHorizontalPadding = 10.dp.roundToPx()
    val textHorizontalSpacing = 2.dp.roundToPx()

    val textXOffset = labelAlignment.align(textSize.width, availableLabelSpace, LayoutDirection.Ltr)

    val shape = GenericShape { size, _ ->
        lineTo((textXOffset + textHorizontalPadding.toFloat()) - textHorizontalSpacing, 0f)
        relativeLineTo(0f, textSize.height.toFloat())
        relativeLineTo(textSize.width.toFloat() + textHorizontalSpacing, 0f)
        relativeLineTo(0f, -textSize.height.toFloat())
        lineTo(size.width, 0f)
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
    }

    Layout(
        modifier = modifier,
        content = {
            Text(
                text = label,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Box(
                Modifier
                    .clip(shape)
                    .groupingBorder()
                    .padding(
                        start = 8.dp,
                        top = textSize.height.toDp(),
                        end = 8.dp,
                        bottom = 8.dp
                    )
            ) {
                content()
            }
        },
        measurePolicy = { measurables, constraints ->
            val textMaxWidth = if (constraints.hasBoundedWidth)
                constraints.maxWidth - (textHorizontalPadding * 2)
            else Constraints.Infinity
            val textConstraints = Constraints(
                maxWidth = textMaxWidth,
                maxHeight = constraints.maxHeight
            )
            val textPlaceable = measurables[0].measure(textConstraints)
            check(textPlaceable.height != 0) { "Text height is 0. Missing grouping label?" }
            val halfTextSize = (textPlaceable.height / 2)
            textSize = IntSize(textPlaceable.width, textPlaceable.height)

            val maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight - halfTextSize
            else Constraints.Infinity
            val minHeight = if (constraints.hasBoundedHeight) maxHeight
            else constraints.minHeight
            val contentConstraints = constraints.copy(
                minHeight = minHeight,
                maxHeight = maxHeight
            )
            val contentPlaceable = measurables[1].measure(contentConstraints)

            val labelSpace = contentPlaceable.width - (textHorizontalPadding * 2)
            availableLabelSpace = labelSpace

            val width = max(contentPlaceable.width, textPlaceable.width)
            val height = contentPlaceable.height + halfTextSize
            layout(width, height) {
                val textX = labelAlignment.align(
                    textPlaceable.width,
                    labelSpace,
                    LayoutDirection.Ltr
                )
                textPlaceable.place(textX + textHorizontalPadding, 0)
                contentPlaceable.place(0, halfTextSize)
            }
        }
    )

}