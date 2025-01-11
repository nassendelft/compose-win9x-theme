package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import nl.ncaj.theme.win9x.vector.Cross
import nl.ncaj.theme.win9x.vector.Icons
import nl.ncaj.theme.win9x.vector.Maximize
import nl.ncaj.theme.win9x.vector.Minimize
import nl.ncaj.theme.win9x.vector.RestoreWindow

internal val AppWindowState = compositionLocalOf { WindowState() }

@Composable
fun Window(
    title: String,
    onCloseRequested: () -> Unit,
    modifier: Modifier = Modifier,
    closeEnabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    menuBar: (MenuBarScope.() -> Unit)? = null,
    statusBar: (StatusBarScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val windowState = rememberWindowState()
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    CompositionLocalProvider(AppWindowState provides windowState) {
        androidx.compose.ui.window.Window(
            onCloseRequest = onCloseRequested,
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
                        modifier = Modifier.pointerInput(Unit) {
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
                        }.pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    windowState.placement =
                                        if (windowState.placement == WindowPlacement.Maximized)
                                            WindowPlacement.Floating else WindowPlacement.Maximized
                                }
                            )
                        },
                    ) {
                        TitleButton(
                            painter = rememberVectorPainter(Icons.Minimize),
                            contentDescription = "minimize window",
                            onClick = { windowState.isMinimized = true }
                        )
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
}
