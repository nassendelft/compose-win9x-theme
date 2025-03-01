package nl.ncaj.theme.win9x.controls

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
    menu: @Composable MenuScope.(menuId: Any) -> Unit,
)  {
    Popup(
        offset = offset,
        onDismissRequest = onDismissRequested,
        content = { Menu(menu = menu) },
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false
        )
    )
}
