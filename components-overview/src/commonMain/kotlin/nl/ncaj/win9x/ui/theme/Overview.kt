package nl.ncaj.win9x.ui.theme

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.controls.Button
import nl.ncaj.win9x.ui.theme.controls.TabHost
import nl.ncaj.win9x.ui.theme.controls.Text
import nl.ncaj.win9x.ui.theme.controls.TreeView
import nl.ncaj.win9x.ui.theme.controls.Window

@Composable
fun Overview(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Window(
        title = "Win9x theme for compose Component Overview",
        statusBar = {
            segment(weight = 1f) {
                Text("StatusBar")
            }
        },
        modifier = modifier
    ) {
        Row {
            TreeView(
                modifier = Modifier.fillMaxHeight()
                    .width(200.dp)
            ) {
                Item(
                    "Components",
                ) {
                    Item("Button")
                }
            }

            TabHost(
                modifier = Modifier.fillMaxHeight()
                    .weight(1f),
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it },
                tabs = { tab { Text("Default") } }
            ) {
                Button(onClick = {}) { Text("Default") }
            }
        }
    }
}