import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.win9x.ui.theme.ControlsOverview
import nl.ncaj.win9x.ui.theme.ControlsPreview
import nl.ncaj.win9x.ui.theme.Win9xTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() = CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    ControlsPreview()
}