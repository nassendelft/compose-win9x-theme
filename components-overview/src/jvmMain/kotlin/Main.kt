import androidx.compose.ui.window.application
import nl.ncaj.theme.win9x.Overview
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.component.IcoImage
import nl.ncaj.theme.win9x.controls.Window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import win9x.components_overview.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Win9xTheme {
        Window(
            title = "Win9x theme for Jetpack Compose - Component Overview",
            icon = { IcoImage({ Res.readBytes("files/directory_open.ico") }, null) },
            onCloseRequested = ::exitApplication,
        ) {
            Overview()
        }
    }
}