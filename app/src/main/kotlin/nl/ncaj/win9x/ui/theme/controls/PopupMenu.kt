package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt

@Preview
@Composable
fun PopupPreview() {
    var showPopupAt by remember { mutableStateOf(IntOffset.Zero) }

    Box(
        modifier = Modifier.fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    showPopupAt = IntOffset(it.x.roundToInt(), it.y.roundToInt())
                }
            },
    )

    if (showPopupAt != IntOffset.Zero) {
        PopupMenu(
            offset = showPopupAt,
            onDismissRequested = { showPopupAt = IntOffset.Zero }
        ) {
            label("Command 1") {}
            divider()
            label("Command 2", enabled = false) {}
        }
    }
}

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
