package nl.ncaj.theme.win9x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.ncaj.theme.win9x.component.IcoImage
import nl.ncaj.theme.win9x.controls.Text
import nl.ncaj.theme.win9x.controls.Window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import win9x.components_overview.generated.resources.Res

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPreview()
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Preview
@Composable
fun AndroidPreview() {
    Win9xTheme {
        Window(
            title = "Component Overview",
            icon = { IcoImage({ Res.readBytes("files/directory_open.ico") }, null) },
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