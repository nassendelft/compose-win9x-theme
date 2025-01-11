package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PopupMenu(
    offset: IntOffset,
    onDismissRequested: () -> Unit,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit,
    content: @Composable MenuScope.() -> Unit
)  {
    Box {
        Popup(
            offset = offset,
            onDismissRequest = onDismissRequested,
            content = { Menu(content = content, subMenu = subMenu) },
            properties = PopupProperties(
                focusable = true,
                usePlatformDefaultWidth = false
            )
        )
    }
}
