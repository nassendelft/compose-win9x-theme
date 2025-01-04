package nl.ncaj.theme.win9x.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.window.WindowDecoration

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PopupMenu(
    onDismissRequested: () -> Unit,
    modifier: Modifier,
    offset: IntOffset,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit,
    content: @Composable MenuScope.() -> Unit
) {
    DialogWindow(
        onDismissRequested,
        decoration = WindowDecoration.Undecorated(),
        transparent = true,
        content = { Menu(modifier, subMenu, content) }
    )
//    Popup(
//        offset = offset,
//        onDismissRequest = onDismissRequested,
//        content = { Menu(modifier, subMenu, content) },
//        properties = PopupProperties(
//            focusable = true,
//            usePlatformDefaultWidth = false
//        )
//    )
}