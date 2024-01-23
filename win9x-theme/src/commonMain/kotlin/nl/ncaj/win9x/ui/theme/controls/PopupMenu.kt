package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PopupMenu(
    offset: IntOffset = IntOffset.Zero,
    onDismissRequested: () -> Unit,
    content: MenuScope.() -> Unit,
) {
    Popup(
        offset = offset,
        onDismissRequest = onDismissRequested,
        content = { Menu(content = content) },
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false
        )
    )
}
