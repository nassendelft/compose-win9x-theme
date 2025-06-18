package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.*

class DetailsListViewScope internal constructor() {
    internal val heading = mutableListOf<@Composable (column: Int) -> Unit>()
    internal val rows = mutableListOf<@Composable (column: Int) -> Unit>()

    fun headingRow(content: @Composable (column: Int) -> Unit) {
        heading += content
    }

    fun itemRow(content: @Composable (column: Int) -> Unit) {
        rows += content
    }
}

private class DetailsListViewState(
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
private fun ListHeading(
    state: DetailsListViewState,
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
private fun ListContent(
    scope: DetailsListViewScope,
    columns: Int,
    state: DetailsListViewState
) {
    Layout(
        content = {
            for (row in scope.rows) {
                for (column in 0 until columns) {
                    Box(
                        modifier = Modifier.width(state.columnWidths[column]),
                        content = { row(column) }
                    )
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
fun DetailsListView(
    columns: Int,
    modifier: Modifier = Modifier,
    content: DetailsListViewScope.() -> Unit
) {
    val density = LocalDensity.current
    val scope = DetailsListViewScope().apply(content)
    val state = remember(density, columns) { DetailsListViewState(density, columns) }

    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    ScrollableHost(
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScroll),
        verticalScrollbarAdapter = rememberScrollbarAdapter(verticalScroll),
        modifier = modifier.sunkenBorder(),
    ) {
        Box(
            modifier.background(Color.White)
                .clickable {} // forces focus to be removed from child if not directly clicked on a child
        ) {
            Column(
                modifier = Modifier.horizontalScroll(horizontalScroll)
                    .verticalScroll(verticalScroll)
            ) {
                Row {
                    scope.heading.forEach { row ->
                        for (column in 0 until columns) {
                            ListHeading(state, column, row)
                        }
                    }
                }
                ListContent(scope, columns, state)
            }

            if (state.isDragging) {
                DashedVerticalLine(modifier = Modifier.fillMaxHeight().offset(x = state.dragX))
            }
        }
    }
}

@Composable
fun ColumnHeading(
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onclick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borders = defaultButtonBorders()
    Row(
        horizontalArrangement = horizontalArrangement,
        modifier = modifier
            .defaultMinSize(minHeight = 15.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onclick
            )
            .background(Win9xTheme.colorScheme.buttonFace)
            .then(
                if (isPressed) Modifier.win9xBorder(borders.pressed)
                else Modifier.win9xBorder(borders.normal)
            )
            .then(
                if (label.isEmpty()) Modifier
                else Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )
            .then(if (isPressed) Modifier.offset(1.dp, 1.dp) else Modifier),
    ) {
        leadingIcon?.invoke()
        Text(
            text = label,
            style = Win9xTheme.typography.default.copy(fontSize = 12.sp),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun DetailsViewListItem(
    label: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    selectable: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onclick: () -> Unit = {},
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Row(
        modifier = modifier
            .then(
                if (selectable) Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                ) { onclick() }
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Box(
                modifier = Modifier.sizeIn(maxWidth = 18.dp, maxHeight = 18.dp),
                content = { icon() }
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = label,
            style = textStyle.copy(fontSize = 12.sp),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .focusDashIndication(interactionSource)
                .selectionBackground(isFocused)
                .padding(horizontal = 1.dp, vertical = 2.dp)
        )
    }
}