package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun PopupMenu(
    offset: IntOffset,
    onDismissRequested: () -> Unit,
    menu: @Composable MenuScope.(MenuState) -> Unit,
) {
    Popup(
        offset = offset,
        onDismissRequest = onDismissRequested,
        content = {
            Menu(
                modifier = Modifier.clickable { onDismissRequested() },
                content = menu
            )
        },
        properties = PopupProperties(
            focusable = true,
            usePlatformDefaultWidth = false
        )
    )
}