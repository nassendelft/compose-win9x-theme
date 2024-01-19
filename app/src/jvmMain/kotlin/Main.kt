import androidx.compose.ui.window.application
import nl.ncaj.win9x.ui.theme.ControlsOverview
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.controls.Window

fun main() = application {
    Window(
        title = "Win9x theme for Jetpack Compose",
        onCloseRequested = ::exitApplication,
    ) {
        Win9xTheme {
            ControlsOverview()
        }
    }
}