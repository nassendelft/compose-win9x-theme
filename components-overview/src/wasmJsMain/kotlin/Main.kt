import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.win9x.ui.theme.Overview
import nl.ncaj.win9x.ui.theme.Win9xTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() = CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    Win9xTheme {
        Overview(Modifier.fillMaxSize())
    }
}