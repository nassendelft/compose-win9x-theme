import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import nl.ncaj.compose.resource.ico.IcoImage
import nl.ncaj.theme.win9x.Overview
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.components_overview.generated.resources.Res
import nl.ncaj.theme.win9x.components_overview.generated.resources.ico
import nl.ncaj.theme.win9x.controls.*
import org.jetbrains.compose.resources.ExperimentalResourceApi

data object MainMenu

@OptIn(ExperimentalResourceApi::class)
fun main() = application {
    Win9xTheme {
        var showAboutDialog by remember { mutableStateOf(false) }
        Window(
            title = "Win9x theme for Jetpack Compose - Component Overview",
            icon = { IcoImage(Res.ico.directory_open, null) },
            onCloseRequested = ::exitApplication,
            resizable = false,
            maximizable = false,
            windowState = rememberWindowState(size = DpSize(675.dp, 600.dp)),
            menuBar = {
                var selectedMenu by remember { mutableStateOf<Any?>(null) }
                MenuBar(
                    selectedMenu = selectedMenu,
                    onMenuSelected = { selectedMenu = it },
                    menu = { state ->
                        MenuItemLabel(
                            label = "About",
                            modifier = Modifier.menuItem(0),
                            selected = state.selectedItem == 0,
                        ) {
                            showAboutDialog = true
                            selectedMenu = null
                        }
                    },
                ) {
                    var selection by remember { mutableStateOf(-1) }
                    val selected = selection == 0 || selectedMenu == MainMenu
                    Text(
                        text = "Help",
                        modifier = Modifier.menuBarItem(
                            key = MainMenu,
                            selected = selected,
                            onSelectionChanged = { selection = if (it) 0 else -1 },
                        ),
                        style = TextDefaults.style(selected = selected).copy(fontSize = 12.sp),
                    )
                }
            },
            content = { Overview() }
        )

        if (showAboutDialog) {
            Window(
                title = "About",
                onCloseRequested = { showAboutDialog = false },
                resizable = false,
                maximizable = false,
                minimizable = false,
                windowState = rememberWindowState(
                    size = DpSize(500.dp, 200.dp),
                    position = WindowPosition.Aligned(Alignment.Center)
                ),
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Win9x Theme for Jetpack Compose")
                    Text("by Nick Assendelft")
                }
            }
        }
    }
}