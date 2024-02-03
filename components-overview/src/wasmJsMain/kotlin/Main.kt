import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.theme.win9x.Overview
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.controls.Text
import nl.ncaj.theme.win9x.controls.Window

@OptIn(ExperimentalComposeUiApi::class)
fun main() = CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    Win9xTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Window(
                title = "Win9x theme for Jetpack Compose - Component Overview",
                statusBar = {
                    segment(weight = 1f) {
                        Text("StatusBar")
                    }
                },
            ) {
                Overview()
            }
        }
    }
}