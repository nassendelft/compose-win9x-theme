package nl.ncaj.win9x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.ncaj.win9x.ui.theme.Overview
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.controls.Text
import nl.ncaj.win9x.ui.theme.controls.Window

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidPreview()
        }
    }
}

@Preview
@Composable
fun AndroidPreview() {
    Win9xTheme {
        Window(
            title = "Component Overview",
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