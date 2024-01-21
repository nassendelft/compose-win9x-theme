import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.win9x.ui.theme.ControlsPreview

@OptIn(ExperimentalComposeUiApi::class)
fun main() = CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    ControlsPreview()
}