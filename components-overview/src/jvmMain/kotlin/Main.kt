import androidx.compose.ui.window.application
import nl.ncaj.theme.win9x.Overview
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.controls.Window

fun main() = application {
    Win9xTheme {
        Window(
            title = "Win9x theme for Jetpack Compose - Component Overview",
            onCloseRequested = ::exitApplication,
        ) {
            Overview()
        }
    }
}