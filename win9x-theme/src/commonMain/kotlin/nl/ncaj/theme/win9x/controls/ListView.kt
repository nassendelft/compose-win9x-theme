package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.DashFocusIndication.Companion.dashFocusIndication
import nl.ncaj.theme.win9x.DashedVerticalLine
import nl.ncaj.theme.win9x.SelectionIndication.Companion.selectionIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder
import nl.ncaj.theme.win9x.win9xBorder
import kotlin.math.max

internal class ListViewItem internal constructor(
    val labels: List<String>,
    val icon: Painter,
    val enabled: Boolean,
    val onClick: () -> Unit,
)

class ListViewColumn(
    val label: String,
    val icon: Painter? = null,
    val horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    val onclick: () -> Unit = {},
)

private val defaultColumn = ListViewColumn("Name")

class ListViewScope internal constructor(private val columnSize: Int) {
    internal val items = mutableListOf<ListViewItem>()

    fun item(
        label: String,
        icon: Painter,
        enabled: Boolean = true,
        onClick: () -> Unit = {},
    ) = item(listOf(label), icon, enabled, onClick)

    fun item(
        labels: List<String>,
        icon: Painter,
        enabled: Boolean = true,
        onClick: () -> Unit = {},
    ) {
        check(labels.size == columnSize) { "The number of labels should match the number of columns" }
        items.add(ListViewItem(labels, icon, enabled, onClick))
    }
}

enum class ListViewViewState { LargeIcons, SmallIcons, List, Details }

class ListViewState(viewState: ListViewViewState = ListViewViewState.LargeIcons) {
    var viewState by mutableStateOf(viewState)
}

@Composable
fun rememberListViewState(viewState: ListViewViewState = ListViewViewState.LargeIcons) =
    remember { ListViewState(viewState) }

@Composable
fun ListView(
    modifier: Modifier = Modifier,
    columns: List<ListViewColumn> = listOf(defaultColumn),
    state: ListViewState = rememberListViewState(),
    content: ListViewScope.() -> Unit
) {
    val items = ListViewScope(columns.size).apply(content).items

    when (state.viewState) {
        ListViewViewState.LargeIcons -> FlowRowListView(modifier) {
            items.forEach { item -> LargeIconItem(item.labels.first(), item.icon, item.enabled) }
        }

        ListViewViewState.SmallIcons -> FlowRowListView(modifier) {
            items.forEach { item -> SmallIconItem(item.labels.first(), item.icon, item.enabled) }
        }

        ListViewViewState.List -> FlowColumnListView(modifier) {
            items.forEach { item -> SmallIconItem(item.labels.first(), item.icon, item.enabled) }
        }

        ListViewViewState.Details -> DetailsListView(columns, items, modifier)
    }
}

@Composable
private fun DetailsItem(
    labels: List<String>,
    icon: Painter,
    enabled: Boolean,
    onClick: () -> Unit,
    columnWidths: List<Dp>,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    Row(modifier) {
        labels.forEachIndexed { index, label ->
            if (index == 0) {
                val textStyle = when {
                    !enabled -> Win9xTheme.typography.disabled
                    isFocused -> Win9xTheme.typography.caption
                    else -> Win9xTheme.typography.default
                }

                Row(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { onClick() }
                        .width(columnWidths[index])
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = icon,
                        modifier = Modifier.size(18.dp),
                        contentDescription = ""
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = label,
                        style = textStyle.copy(fontSize = 12.sp),
                        minLines = 1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .selectionIndication(interactionSource)
                            .dashFocusIndication(interactionSource)
                            .padding(horizontal = 1.dp, vertical = 2.dp)
                    )
                }
            } else {
                Text(
                    text = label,
                    style = Win9xTheme.typography.default.copy(fontSize = 12.sp),
                    modifier = Modifier.width(columnWidths[index]).padding(horizontal = 4.dp),
                    minLines = 1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LargeIconItem(
    label: String,
    icon: Painter,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onclick: () -> Unit = {},
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Column(
        modifier = modifier.size(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onclick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = icon,
            modifier = Modifier.size(36.dp),
            contentDescription = ""
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = textStyle.copy(fontSize = 12.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .selectionIndication(interactionSource)
                .dashFocusIndication(interactionSource)
                .padding(horizontal = 1.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun SmallIconItem(
    label: String,
    icon: Painter,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onclick: () -> Unit = {},
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Box(modifier = modifier.defaultMinSize(minWidth = 100.dp)) {
        Row(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onclick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = icon,
                modifier = Modifier.size(18.dp),
                contentDescription = ""
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                style = textStyle.copy(fontSize = 12.sp),
                modifier = Modifier
                    .selectionIndication(interactionSource)
                    .dashFocusIndication(interactionSource)
                    .padding(horizontal = 1.dp, vertical = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowColumnListView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val horizontalScroll = rememberScrollState()
    ScrollableHost(
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScroll),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        FlowColumn(
            modifier = modifier
                .background(Color.White)
                .horizontalScroll(horizontalScroll),
            content = { content() }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowListView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val verticalScroll = rememberScrollState()
    ScrollableHost(
        verticalScrollbarAdapter = rememberScrollbarAdapter(verticalScroll),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        FlowRow(
            modifier = modifier
                .background(Color.White)
                .verticalScroll(verticalScroll),
            content = { content() },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailsListView(
    columns: List<ListViewColumn>,
    items: List<ListViewItem>,
    modifier: Modifier = Modifier,
) = with(LocalDensity.current) {
    val defaultColumnWidth = 100.dp
    val columnWidths = mutableStateListOf(*columns.map { defaultColumnWidth }.toTypedArray())
    var listWidth by remember { mutableIntStateOf(0) }
    val lastColumnWidth by derivedStateOf { max(0, listWidth - columnWidths.sumOf { it.roundToPx() }) }
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var offsetX by remember { mutableStateOf(0f) }

    val columnState = rememberLazyListState()
    val horizontalScroll = rememberScrollState()
    ScrollableHost(
        verticalScrollbarAdapter = rememberScrollbarAdapter(columnState),
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScroll),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .onSizeChanged { listWidth = it.width }
                .horizontalScroll(horizontalScroll),
        ) {
            LazyColumn(
                state = columnState,
            ) {
                stickyHeader {
                    Row(
                        modifier = Modifier.height(IntrinsicSize.Max)
                    ) {
                        columns.forEachIndexed { index, column ->
                            Box {
                                ColumnHeading(
                                    label = column.label,
                                    icon = column.icon,
                                    horizontalArrangement = column.horizontalArrangement,
                                    onclick = column.onclick,
                                    modifier = Modifier.fillMaxHeight().width(columnWidths[index]),
                                )
                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                        .fillMaxHeight()
                                        .align(Alignment.TopEnd)
                                        .pointerHoverIcon(PointerIcon.Crosshair)
                                        .draggable(
                                            state = rememberDraggableState { delta ->
                                                offsetX += delta
                                            },
                                            orientation = Orientation.Horizontal,
                                            startDragImmediately = true,
                                            onDragStarted = { draggingIndex = index },
                                            onDragStopped = {
                                                columnWidths[index] =
                                                    max(0f, columnWidths[index].toPx() + offsetX).toDp()
                                                draggingIndex = -1
                                                offsetX = 0f
                                            }
                                        )
                                )
                            }
                        }
                        ColumnHeading(
                            label = "",
                            modifier = Modifier.fillMaxHeight().width(lastColumnWidth.toDp()),
                        )
                    }
                }
                items(items) { item ->
                    DetailsItem(
                        labels = item.labels,
                        icon = item.icon,
                        enabled = item.enabled,
                        onClick = item.onClick,
                        columnWidths = columnWidths,
                    )
                }
            }
            if (draggingIndex != -1) {
                val x = columnWidths.take(draggingIndex + 1).sumOf { it.roundToPx() } + offsetX
                DashedVerticalLine(
                    modifier = Modifier.fillMaxHeight()
                        .offset(x = x.toDp())
                )
            }
        }
    }
}

@Composable
private fun ColumnHeading(
    label: String,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
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
        icon?.let { Image(icon, contentDescription = null) }
        Text(
            text = label,
            style = Win9xTheme.typography.default.copy(fontSize = 12.sp),
            maxLines = 1,
            minLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
