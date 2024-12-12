package nl.ncaj.theme.win9x.component

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.DashedVerticalLine

class TableScope internal constructor() {
    internal val heading = mutableListOf<@Composable (column: Int) -> Unit>()
    internal val rows = mutableListOf<@Composable (column: Int) -> Unit>()

    fun headingRow(content: @Composable (column: Int) -> Unit) {
        heading += content
    }

    fun itemRow(content: @Composable (column: Int) -> Unit) {
        rows += content
    }
}

private class TableState(
    private val density: Density,
    columns: Int,
    initialColumnsWidth: Dp = 100.dp
) {
    private var offsetX = 0f
    private var selectedIndex = -1

    var isDragging by mutableStateOf(false)
    val columnWidths = (0 until columns).map { initialColumnsWidth }.toMutableStateList()
    var dragX by mutableStateOf(0.dp)

    fun onDragStarted(index: Int) {
        selectedIndex = index
        isDragging = true
    }

    fun onDrag(delta: Float) = with(density) {
        offsetX += delta
        dragX = (columnWidths.take(selectedIndex + 1).sumOf { it.roundToPx() } + offsetX).toDp()
    }

    fun onDragStopped() = with(density) {
        columnWidths[selectedIndex] = maxOf(4.dp, columnWidths[selectedIndex] + offsetX.toDp())
        offsetX = 0f
        selectedIndex = -1
        isDragging = false
    }
}

@Composable
private fun TableHeading(
    state: TableState,
    column: Int,
    content: @Composable (column: Int) -> Unit
) {
    Box(modifier = Modifier.width(state.columnWidths[column]).height(IntrinsicSize.Max)) {
        content(column)
        Spacer(
            modifier = Modifier.width(4.dp)
                .fillMaxHeight()
                .align(Alignment.TopEnd)
                .pointerHoverIcon(PointerIcon.Crosshair)
                .draggable(
                    state = rememberDraggableState(state::onDrag),
                    orientation = Orientation.Horizontal,
                    startDragImmediately = true,
                    onDragStarted = { state.onDragStarted(column) },
                    onDragStopped = { state.onDragStopped() },
                )
        )
    }
}

@Composable
private fun TableContent(
    scope: TableScope,
    columns: Int,
    state: TableState
) {
    Layout(
        content = {
            scope.rows.forEach { row ->
                for (column in 0 until columns) {
                    Box(modifier = Modifier.width(state.columnWidths[column]), content = { row(column) })
                }
            }
        },
        measurePolicy = { measurables, constraints ->
            val measurableRows = measurables.chunked(columns)
            val placeables = mutableListOf<Placeable>()
            var maxHeight = constraints.maxHeight
            for (row in measurableRows) {
                var maxWidth = constraints.maxWidth
                var height = 0
                for (column in row) {
                    val itemConstraints = constraints.copy(maxWidth = maxWidth, maxHeight = maxHeight)
                    val placeable = column.measure(itemConstraints)
                    height = maxOf(placeable.height, height)
                    placeables.add(placeable)
                    if (constraints.hasBoundedWidth) maxWidth -= placeable.width
                }
                if (constraints.hasBoundedHeight) maxHeight -= height
            }

            val placeableRows = placeables.chunked(columns)
            val width = minOf(constraints.maxWidth, placeableRows.first().sumOf { it.width })
            val height = minOf(constraints.maxHeight, placeableRows.sumOf { row -> row.maxOf { it.height } })
            layout(width, height) {
                var y = 0
                for (placeableRow in placeableRows) {
                    var x = 0
                    for (placeable in placeableRow) {
                        placeable.place(x, y)
                        x += placeable.width
                    }
                    y += placeableRow.maxOf { it.height }
                }
            }
        },
    )
}

@Composable
fun Table(
    columns: Int,
    modifier: Modifier = Modifier,
    verticalScrollState: ScrollState? = null,
    horizontalScrollState: ScrollState? = null,
    content: TableScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = TableScope().apply(content)
    val state = remember(density, columns) { TableState(density, columns) }

    val verticalScrollModifier = verticalScrollState?.let { Modifier.verticalScroll(it) } ?: Modifier
    val horizontalScrollModifier = horizontalScrollState?.let { Modifier.horizontalScroll(it) } ?: Modifier

    Box(modifier) {
        Column(Modifier.then(horizontalScrollModifier).then(verticalScrollModifier)) {
            Row {
                scope.heading.forEach { row ->
                    for (column in 0 until columns) {
                        TableHeading(state, column, row)
                    }
                }
            }
            TableContent(scope, columns, state)
        }

        if (state.isDragging) {
            DashedVerticalLine(modifier = Modifier.fillMaxHeight().offset(x = state.dragX))
        }
    }
}