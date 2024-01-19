package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import nl.ncaj.win9x.ui.theme.icCrossPainter
import nl.ncaj.win9x.ui.theme.icMaximizePainter
import nl.ncaj.win9x.ui.theme.icMinimizePainter
import nl.ncaj.win9x.ui.theme.icRestoreWindowPainter

@OptIn(ExperimentalFoundationApi::class)
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
    val density = LocalDensity.current
    val windowState = rememberWindowState()

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
                    modifier = Modifier.onDrag {
                        windowState.position = WindowPosition(
                            x = windowState.position.x + with(density) { it.x.toDp() },
                            y = windowState.position.y + with(density) { it.y.toDp() },
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
                        painter = icMinimizePainter(),
                        contentDescription = "minimize window",
                        onClick = { windowState.isMinimized = true }
                    )
                    if (windowState.placement == WindowPlacement.Maximized) {
                        TitleButton(
                            painter = icRestoreWindowPainter(),
                            contentDescription = "restore window size",
                            onClick = { windowState.placement = WindowPlacement.Floating }
                        )
                    } else {
                        TitleButton(
                            painter = icMaximizePainter(),
                            contentDescription = "maximize window",
                            onClick = { windowState.placement = WindowPlacement.Maximized }
                        )
                    }
                    Spacer(Modifier.width(1.dp))
                    TitleButton(
                        painter = icCrossPainter(),
                        contentDescription = "Close window",
                        onClick = onCloseRequested,
                        enabled = closeEnabled
                    )
                }
            }
        )
    }
}
