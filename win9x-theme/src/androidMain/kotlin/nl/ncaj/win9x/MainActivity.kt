package nl.ncaj.win9x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import nl.ncaj.win9x.ui.theme.ControlsPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlsPreview()
        }
    }
}

@Preview
@Composable
fun AndroidPreview() {
    ControlsPreview()
}