package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import nl.ncaj.theme.win9x.vector.*

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
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    androidx.compose.ui.window.Window(
        onCloseRequest = onCloseRequested,
        resizable = resizable,
        undecorated = true,
        state = windowState
    ) {
        Window(
            modifier = modifier,
            menuBar = menuBar,
            statusBar = statusBar,
            content = content,
            titleBar = {
                TitleBar(
                    title = title,
                    icon = icon,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { dragOffset = Offset.Zero },
                                onDrag = { change, _ ->
                                    dragOffset += change.position - change.previousPosition
                                    windowState.position = WindowPosition(
                                        x = windowState.position.x + dragOffset.x.toDp(),
                                        y = windowState.position.y + dragOffset.y.toDp(),
                                    )
                                }
                            )
                        }
                        .then(
                            if (maximizable) Modifier
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = {
                                            windowState.placement =
                                                if (windowState.placement == WindowPlacement.Maximized) WindowPlacement.Floating
                                                else WindowPlacement.Maximized
                                        }
                                    )
                                }
                            else Modifier
                        ),
                ) {
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
}
