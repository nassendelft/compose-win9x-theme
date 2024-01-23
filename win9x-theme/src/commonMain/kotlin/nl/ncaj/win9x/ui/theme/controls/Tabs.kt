package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.win9xBorder
import kotlin.math.max
import kotlin.math.min

@Composable
internal fun TabsPreview() {
    var selectedTabIndex by remember { mutableIntStateOf(1) }

    Column {
        Text("- Tabs -")
        Spacer(modifier = Modifier.height(2.dp))

        TabHost(
            selectedTabIndex = selectedTabIndex,
            onTabSelected = { selectedTabIndex = it },
            tabs = { for (i in 0..2) tab { Text("Tab ${i + 1}") } },
            content = {
                Text(
                    text = "Tab selected: ${selectedTabIndex + 1}",
                    modifier = Modifier.widthIn(150.dp)
                )
            },
        )
    }
}

class TabScope {
    internal val tabs = mutableListOf<@Composable BoxScope.() -> Unit>()

    fun tab(content: @Composable BoxScope.() -> Unit) {
        tabs.add(content)
    }
}

@Composable
fun TabHost(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    tabs: TabScope.() -> Unit,
    content: @Composable () -> Unit
) = with(LocalDensity.current) {
    val offset = 1.dp
    val scope = TabScope().apply(tabs)

    val measurePolicy = remember(selectedTabIndex) {
        TabMeasurePolicy(selectedTabIndex, offset.roundToPx())
    }

    Layout(
        modifier = modifier
            .defaultMinSize(minHeight = 50.dp),
        content = {
            scope.tabs.forEachIndexed { i, tab ->
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .tabBorder(offset)
                        .padding(Win9xTheme.borderWidthDp + 4.dp)
                        .then(if (selectedTabIndex == i) Modifier.zIndex(1f) else Modifier)
                        .clickable { onTabSelected(i) },
                    content = tab
                )
            }
            Box(
                Modifier
                    .background(Win9xTheme.colorScheme.buttonFace)
                    .tabsContentBorder()
                    .padding(4.dp)
                    .zIndex(1f)
            ) {
                content()
            }
        },
        measurePolicy = measurePolicy
    )
}

private class TabMeasurePolicy(
    private val selectedTabIndex: Int,
    private val offset: Int
) : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val tabMeasurables = measurables.subList(0, measurables.size - 1)

        val maxIntrinsicHeightTabs =
            offset + tabMeasurables.maxOf { it.maxIntrinsicHeight(constraints.maxWidth) }

        val placeables = tabMeasurables.mapIndexed { index, measurable ->
            val width =
                measurable.maxIntrinsicWidth(constraints.maxHeight) +
                        (if (selectedTabIndex == index) offset * 2 else 0)

            val tabConstraints = Constraints.fixed(width, maxIntrinsicHeightTabs)

            measurable.measure(tabConstraints)
        }

        val fullWidthTabs = placeables.fold(0) { acc, placeable -> acc + placeable.width }
        val maxHeightContent =
            if (constraints.hasBoundedHeight) constraints.maxHeight - maxIntrinsicHeightTabs
            else Constraints.Infinity

        val contentConstraints = if (constraints.hasBoundedHeight) {
            Constraints.fixed(
                max(fullWidthTabs, constraints.minWidth),
                min(maxHeightContent, constraints.maxHeight)
            )
        } else {
            Constraints.fixedWidth(max(fullWidthTabs, constraints.minWidth))
        }
        val contentPlaceable = measurables.last().measure(contentConstraints)

        return layout(
            contentPlaceable.width,
            placeables.maxOf { it.height } + contentPlaceable.height - (offset * 2),
        ) {
            contentPlaceable.place(0, maxIntrinsicHeightTabs - offset)

            var x = offset
            placeables.forEachIndexed { index, placeable ->
                var xOffset = if (index == selectedTabIndex) x - offset else x
                xOffset = if (index > selectedTabIndex) x - offset * 2 else xOffset
                placeable.place(xOffset, if (index == selectedTabIndex) 0 else offset)
                x += placeable.width
            }
        }
    }
}

private fun Modifier.tabsContentBorder() = composed {
    win9xBorder(
        outerStartTop = Win9xTheme.colorScheme.buttonHighlight,
        innerStartTop = Win9xTheme.colorScheme.buttonFace,
        innerEndBottom = Win9xTheme.colorScheme.buttonShadow,
        outerEndBottom = Win9xTheme.colorScheme.windowFrame,
        borderWidth = Win9xTheme.borderWidthPx,
    )
}

private fun Modifier.tabBorder(offset: Dp): Modifier = composed {
    val outerStartTop = Win9xTheme.colorScheme.buttonHighlight
    val outerEndBottom = Win9xTheme.colorScheme.windowFrame
    val innerEndBottom = Win9xTheme.colorScheme.buttonShadow
    val background = Win9xTheme.colorScheme.buttonFace
    val borderWidth = Win9xTheme.borderWidthPx
    val offsetPx = with(LocalDensity.current) { offset.toPx() }
    this.drawBehind {
        val strokeWidth = borderWidth / 2
        val halfStrokeWidth = strokeWidth / 2

        // background
        drawPath(
            path = Path().apply {
                moveTo(strokeWidth, size.height)
                lineTo(strokeWidth, strokeWidth + strokeWidth)
                relativeLineTo(strokeWidth + halfStrokeWidth, 0f)
                relativeLineTo(0f, -strokeWidth)
                lineTo(size.width - strokeWidth - strokeWidth, strokeWidth)
                relativeLineTo(0f, strokeWidth)
                relativeLineTo(strokeWidth, 0f)
                lineTo(size.width - strokeWidth, size.height)
            },
            color = background,
        )

        // top left -> bottom left
        drawLine(
            color = outerStartTop,
            start = Offset(halfStrokeWidth, strokeWidth + halfStrokeWidth),
            end = Offset(halfStrokeWidth, size.height - offsetPx),
            strokeWidth = strokeWidth
        )

        // top left corner
        drawLine(
            color = outerStartTop,
            start = Offset(strokeWidth, strokeWidth + halfStrokeWidth),
            end = Offset(
                strokeWidth + strokeWidth + halfStrokeWidth,
                strokeWidth + halfStrokeWidth
            ),
            strokeWidth = strokeWidth
        )

        // top left -> top right
        drawLine(
            color = outerStartTop,
            start = Offset(strokeWidth + halfStrokeWidth + halfStrokeWidth, halfStrokeWidth),
            end = Offset(size.width - borderWidth, halfStrokeWidth),
            strokeWidth = strokeWidth
        )

        // top right corner
        drawLine(
            color = outerEndBottom,
            start = Offset(size.width - borderWidth, strokeWidth + halfStrokeWidth),
            end = Offset(size.width - strokeWidth, strokeWidth + halfStrokeWidth),
            strokeWidth = strokeWidth
        )

        // top right -> bottom right
        drawLine(
            color = outerEndBottom,
            start = Offset(size.width - halfStrokeWidth, strokeWidth + strokeWidth),
            end = Offset(size.width - halfStrokeWidth, size.height - offsetPx),
            strokeWidth = strokeWidth
        )

        // top right -> bottom right (inner)
        drawLine(
            color = innerEndBottom,
            start = Offset(
                size.width - strokeWidth - halfStrokeWidth,
                strokeWidth + halfStrokeWidth + halfStrokeWidth
            ),
            end = Offset(size.width - strokeWidth - halfStrokeWidth, size.height),
            strokeWidth = strokeWidth
        )
    }
}

