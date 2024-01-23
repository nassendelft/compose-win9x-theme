import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.win9x.ui.theme.Overview
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.controls.Text
import nl.ncaj.win9x.ui.theme.controls.Window

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