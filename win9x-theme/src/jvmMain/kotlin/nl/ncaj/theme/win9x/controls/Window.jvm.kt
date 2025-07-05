package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.drawDashFocus
import nl.ncaj.theme.win9x.vector.*
import java.awt.Cursor
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

@Composable
fun Window(
    title: String,
    onCloseRequested: () -> Unit,
    modifier: Modifier = Modifier,
    closeEnabled: Boolean = true,
    maximizable: Boolean = true,
    minimizable: Boolean = true,
    resizable: Boolean = true,
    windowState: WindowState = rememberWindowState(),
    icon: @Composable (() -> Unit)? = null,
    menuBar: @Composable (() -> Unit)? = null,
    statusBar: (StatusBarScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    var updatingWindow by remember { mutableStateOf(false) }
    val dragWindowState = remember {
        WindowState(
            placement = WindowPlacement.Floating,
            isMinimized = false,
            position = windowState.position,
            size = windowState.size
        )
    }

    var barHeight by remember { mutableStateOf(0.dp) }

    Window(
        onCloseRequest = onCloseRequested,
        resizable = false,
        undecorated = true,
        state = windowState
    ) {
        Window(
            modifier = modifier.windowDragHandle(
                enabled = resizable,
                windowState = windowState,
                dragWindowState = dragWindowState,
                handleSize = 4.dp,
                minSize = DpSize(100.dp, barHeight + (Win9xTheme.borderWidthDp * 2)),
                onWindowMoveChange = {
                    updatingWindow = it
                    dragWindowState.position = windowState.position
                },
            ),
            menuBar = menuBar,
            statusBar = statusBar,
            content = content,
            titleBar = {
                val density = LocalDensity.current
                TitleBar(
                    modifier = Modifier.onPlaced { barHeight = with(density) { it.size.height.toDp() } }
                ) {
                    TitleBarIcon(
                        windowState = windowState,
                        onCloseRequested = onCloseRequested,
                        maximizable = maximizable,
                        minimizable = minimizable,
                        icon = icon,
                    )

                    Text(
                        text = title,
                        style = Win9xTheme.typography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 2.dp)
                            .weight(1f)
                            .titleBarGestures(
                                windowState = windowState,
                                dragWindowState = dragWindowState,
                                maximizable = maximizable,
                                onWindowMoveChange = { update -> updatingWindow = update }
                            )
                    )

                    if (minimizable) {
                        TitleButton(
                            painter = rememberVectorPainter(Icons.Minimize),
                            contentDescription = "minimize window",
                            onClick = { windowState.isMinimized = true }
                        )
                    }
                    if (maximizable) {
                        if (windowState.placement == WindowPlacement.Maximized) {
                            TitleButton(
                                painter = rememberVectorPainter(Icons.RestoreWindow),
                                contentDescription = "restore window size",
                                onClick = { windowState.placement = WindowPlacement.Floating }
                            )
                        } else {
                            TitleButton(
                                painter = rememberVectorPainter(Icons.Maximize),
                                contentDescription = "maximize window",
                                onClick = { windowState.placement = WindowPlacement.Maximized }
                            )
                        }
                    }
                    Spacer(Modifier.width(1.dp))
                    TitleButton(
                        painter = rememberVectorPainter(Icons.Cross),
                        contentDescription = "Close window",
                        onClick = onCloseRequested,
                        enabled = closeEnabled
                    )
                }
            }
        )
    }

    if (updatingWindow) {
        Window(
            onCloseRequest = { updatingWindow = false },
            resizable = resizable,
            transparent = true,
            undecorated = true,
            state = dragWindowState,
        ) {
            DisposableEffect(dragWindowState) {
                val windowListener = object : WindowFocusListener {
                    override fun windowGainedFocus(e: WindowEvent?) = Unit
                    override fun windowLostFocus(e: WindowEvent?) {
                        updatingWindow = false
                    }
                }
                window.addWindowFocusListener(windowListener)
                onDispose { window.removeWindowFocusListener(windowListener) }
            }

            Box(
                Modifier.fillMaxSize()
                    .drawBehind { drawDashFocus(stroke = 8.dp) }
                    .pointerHoverIcon(PointerIcon.Crosshair)
                    .clickable { updatingWindow = false }
            )
        }
    }
}

@Composable
private fun TitleBarIcon(
    windowState: WindowState,
    onCloseRequested: () -> Unit,
    maximizable: Boolean = true,
    minimizable: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
) {
    var showWindowMenu by remember { mutableStateOf(false) }
    Column {
        Box(
            Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onCloseRequested() },
                    onTap = { showWindowMenu = true }
                )
            },
            content = { icon?.invoke() }
        )
        if (showWindowMenu) {
            Box {
                PopupMenu(
                    onDismissRequested = { showWindowMenu = false },
                    menu = { state ->
                        if (minimizable) {
                            MenuItemLabel(
                                label = "Minimize",
                                modifier = Modifier.menuItem(3),
                                selected = state.selectedItem == 3,
                            ) { windowState.isMinimized = true }
                        }
                        if (maximizable) {
                            MenuItemLabel(
                                label = "Maximize",
                                modifier = Modifier.menuItem(4),
                                selected = state.selectedItem == 4,
                                onClick = { windowState.isMinimized = false },
                            )
                        }
                        MenuItemDivider()
                        MenuItemLabel(
                            label = "Close",
                            modifier = Modifier.menuItem(5),
                            selected = state.selectedItem == 5,
                            onClick = { onCloseRequested() },
                        )
                    }
                )
            }
        }
    }
}

private fun Modifier.titleBarGestures(
    windowState: WindowState,
    dragWindowState: WindowState,
    maximizable: Boolean,
    onWindowMoveChange: (Boolean) -> Unit,
) = pointerInput(Unit) {
    if (!maximizable) return@pointerInput
    detectTapGestures(
        onDoubleTap = {
            windowState.placement =
                if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating
                else WindowPlacement.Maximized
        }
    )
}.pointerInput(Unit) {
    var dragOffset = Offset.Zero
    detectDragGestures(
        onDragStart = {
            dragOffset = Offset.Zero
            onWindowMoveChange(true)
        },
        onDragEnd = {
            onWindowMoveChange(false)
            windowState.position = dragWindowState.position
        },
        onDrag = { change, _ ->
            dragOffset += change.position - change.previousPosition
            dragWindowState.position = WindowPosition(
                x = windowState.position.x + dragOffset.x.toDp(),
                y = windowState.position.y + dragOffset.y.toDp(),
            )
        }
    )
}

private enum class Direction {
    North,
    East,
    South,
    West,
    NorthEast,
    SouthEast,
    SouthWest,
    NorthWest,
}

@OptIn(ExperimentalComposeUiApi::class)
private fun Modifier.windowDragHandle(
    windowState: WindowState,
    dragWindowState: WindowState,
    handleSize: Dp,
    minSize: DpSize,
    enabled: Boolean,
    onWindowMoveChange: (Boolean) -> Unit,
) = if(!enabled) this else this.composed {
    var direction: Direction? by remember { mutableStateOf(null) }
    var dragging by remember { mutableStateOf(false) }
    val cornerSize = handleSize * 2
    val icon = when (direction) {
        Direction.North -> Cursor.N_RESIZE_CURSOR
        Direction.East -> Cursor.E_RESIZE_CURSOR
        Direction.South -> Cursor.S_RESIZE_CURSOR
        Direction.West -> Cursor.W_RESIZE_CURSOR
        Direction.NorthEast -> Cursor.NE_RESIZE_CURSOR
        Direction.SouthEast -> Cursor.SE_RESIZE_CURSOR
        Direction.SouthWest -> Cursor.SW_RESIZE_CURSOR
        Direction.NorthWest -> Cursor.NW_RESIZE_CURSOR
        null -> Cursor.DEFAULT_CURSOR
    }

    pointerHoverIcon(PointerIcon(Cursor(icon)))
        .onPointerEvent(PointerEventType.Exit) {
            if (dragging) return@onPointerEvent
            direction = null
        }
        .onPointerEvent(PointerEventType.Move) { event ->
            if (dragging) return@onPointerEvent
            val position = event.changes.first().position
            direction = when {
                position.x < cornerSize.toPx() && position.y < cornerSize.toPx() ->
                    Direction.NorthWest

                position.x > windowState.size.width.toPx() - cornerSize.toPx() && position.y < cornerSize.toPx() ->
                    Direction.NorthEast

                position.x < cornerSize.toPx() && position.y > windowState.size.height.toPx() - cornerSize.toPx() ->
                    Direction.SouthWest

                position.x > windowState.size.width.toPx() - cornerSize.toPx() && position.y > windowState.size.height.toPx() - cornerSize.toPx() ->
                    Direction.SouthEast

                position.y < handleSize.toPx() ->
                    Direction.North

                position.x > windowState.size.width.toPx() - handleSize.toPx() ->
                    Direction.East

                position.y > windowState.size.height.toPx() - handleSize.toPx() ->
                    Direction.South

                position.x < handleSize.toPx() ->
                    Direction.West

                else -> null
            }
        }
        .onPointerEvent(PointerEventType.Press) {
            if (direction != null) onWindowMoveChange(true)
        }.onPointerEvent(PointerEventType.Release) { event ->
            onWindowMoveChange(false)
            direction = null
        }.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { if (direction != null) dragging = true },
                onDrag = { change, _ ->
                    if (!dragging) return@detectDragGestures
                    val dragOffset = change.position - change.previousPosition

                    when (direction) {
                        Direction.NorthEast -> {
                            dragWindowState.position = WindowPosition.Absolute(
                                x = dragWindowState.position.x,
                                y = dragWindowState.position.y - -dragOffset.y.toDp()
                            )
                            dragWindowState.size += DpSize(dragOffset.x.toDp(), -dragOffset.y.toDp())
                        }

                        Direction.NorthWest -> {
                            dragWindowState.position = WindowPosition.Absolute(
                                x = dragWindowState.position.x - -dragOffset.x.toDp(),
                                y = dragWindowState.position.y - -dragOffset.y.toDp()
                            )
                            dragWindowState.size += DpSize(-dragOffset.x.toDp(), -dragOffset.y.toDp())
                        }

                        Direction.SouthEast -> {
                            dragWindowState.size += DpSize(dragOffset.x.toDp(), dragOffset.y.toDp())
                        }

                        Direction.SouthWest -> {
                            dragWindowState.position = WindowPosition.Absolute(
                                x = dragWindowState.position.x - -dragOffset.x.toDp(),
                                y = dragWindowState.position.y
                            )
                            dragWindowState.size += DpSize(-dragOffset.x.toDp(), dragOffset.y.toDp())
                        }

                        Direction.North -> {
                            dragWindowState.position = WindowPosition.Absolute(
                                x = dragWindowState.position.x,
                                y = dragWindowState.position.y - -dragOffset.y.toDp()
                            )
                            dragWindowState.size += DpSize(0.dp, -dragOffset.y.toDp())
                        }

                        Direction.East -> {
                            dragWindowState.size += DpSize(dragOffset.x.toDp(), 0.dp)
                        }

                        Direction.South -> {
                            dragWindowState.size += DpSize(0.dp, dragOffset.y.toDp())
                        }

                        Direction.West -> {
                            dragWindowState.position = WindowPosition.Absolute(
                                x = dragWindowState.position.x - -dragOffset.x.toDp(),
                                y = dragWindowState.position.y
                            )
                            dragWindowState.size += DpSize(-dragOffset.x.toDp(), 0.dp)
                        }

                        null -> {
                            /** do nothing **/
                        }
                    }
                    dragWindowState.size = DpSize(
                        dragWindowState.size.width.coerceIn(minSize.width, windowState.size.width),
                        dragWindowState.size.height.coerceIn(minSize.height, windowState.size.height)
                    )
                },
                onDragEnd = {
                    dragging = false
                    windowState.size = dragWindowState.size
                    windowState.position = dragWindowState.position
                }
            )
        }
}