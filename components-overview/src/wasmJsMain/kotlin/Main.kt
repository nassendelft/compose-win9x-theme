import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.CanvasBasedWindow
import nl.ncaj.compose.resource.ico.IcoImage
import nl.ncaj.theme.win9x.Overview
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.components_overview.generated.resources.Res
import nl.ncaj.theme.win9x.components_overview.generated.resources.ico
import nl.ncaj.theme.win9x.controls.Text
import nl.ncaj.theme.win9x.controls.Window
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalComposeUiApi::class, ExperimentalResourceApi::class)
fun main() = CanvasBasedWindow(canvasElementId = "ComposeTarget") {
    Win9xTheme {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Window(
                title = "Win9x theme for Jetpack Compose - Component Overview",
                icon = { IcoImage(Res.ico.directory_open, null) },
                modifier = Modifier.size(675.dp, 600.dp),
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