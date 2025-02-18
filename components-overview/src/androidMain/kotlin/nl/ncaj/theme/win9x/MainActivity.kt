package nl.ncaj.theme.win9x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import nl.ncaj.compose.resource.ico.IcoImage
import nl.ncaj.theme.win9x.components_overview.generated.resources.Res
import nl.ncaj.theme.win9x.components_overview.generated.resources.ico
import nl.ncaj.theme.win9x.controls.Text
import nl.ncaj.theme.win9x.controls.Window
import org.jetbrains.compose.resources.ExperimentalResourceApi

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalResourceApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Win9xTheme {
                Window(
                    title = "Component Overview",
                    icon = { IcoImage(Res.ico.directory_open, null) },
                    statusBar = {
                        segment(weight = 1f) {
                            Text("StatusBar")
                        }
                    },
                    content = { Overview(itemsPerRow = 1) }
                )
            }
        }
    }
}
