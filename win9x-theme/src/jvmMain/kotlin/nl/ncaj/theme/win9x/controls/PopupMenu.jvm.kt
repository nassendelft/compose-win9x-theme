package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowDecoration
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PopupMenu(
    offset: IntOffset,
    onDismissRequested: () -> Unit,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit,
    content: @Composable MenuScope.() -> Unit
) = with(LocalDensity.current) {
    var boxOffset by remember { mutableStateOf(Offset.Unspecified) }

    Box(Modifier.onGloballyPositioned { layoutCoordinates ->
        boxOffset = layoutCoordinates.positionInRoot()
    })

    if (boxOffset == Offset.Unspecified) return

    val appWindowStatePosition = AppWindowState.current.position

    val state = rememberWindowState(
        position = WindowPosition.Absolute(
            boxOffset.x.toDp() + offset.x.toDp() + appWindowStatePosition.x,
            boxOffset.y.toDp() + offset.y.toDp() + appWindowStatePosition.y
        ),
        size = DpSize.Unspecified
    )

    Window(
        state = state,
        onCloseRequest = onDismissRequested,
        transparent = true,
        resizable = false,
        decoration = WindowDecoration.Undecorated(),
        content = {
            val focusListener = remember {
                object : WindowFocusListener {
                    override fun windowGainedFocus(e: WindowEvent?) = Unit
                    override fun windowLostFocus(e: WindowEvent?) = onDismissRequested()
                }
            }
            DisposableEffect(focusListener) {
                window.addWindowFocusListener(focusListener)
                onDispose { window.removeWindowFocusListener(focusListener) }
            }
            Menu(
                modifier = Modifier
                    .background(Color.Red)
                    .onSizeChanged { state.size = DpSize(it.width.toDp(), it.height.toDp()) }
                    .clickable(onClick = onDismissRequested),
                content = content,
                subMenu = subMenu
            )
        }
    )
}