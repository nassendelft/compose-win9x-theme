import androidx.compose.ui.window.application
import nl.ncaj.win9x.ui.theme.Overview
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.controls.Window

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