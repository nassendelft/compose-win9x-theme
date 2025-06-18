package nl.ncaj.theme.win9x

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput

internal fun Modifier.onHover(
    enabled: Boolean = true,
    onHover: (Boolean) -> Unit
) = if(!enabled) Modifier else this.pointerInput(PointerEventType.Enter) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Enter) {
                onHover(true)
            } else if (event.type == PointerEventType.Exit) {
                onHover(false)
            }
        }
    }
}