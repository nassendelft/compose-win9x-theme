package nl.ncaj.win9x.ui.theme

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.controls.ButtonPreview
import nl.ncaj.win9x.ui.theme.controls.CheckboxPreview
import nl.ncaj.win9x.ui.theme.controls.DropDownListBoxPreview
import nl.ncaj.win9x.ui.theme.controls.GroupingPreview
import nl.ncaj.win9x.ui.theme.controls.ListBoxPreview
import nl.ncaj.win9x.ui.theme.controls.MenuButtonPreview
import nl.ncaj.win9x.ui.theme.controls.OptionButtonPreview
import nl.ncaj.win9x.ui.theme.controls.OptionSetButtonPreview
import nl.ncaj.win9x.ui.theme.controls.ProgressIndicatorPreview
import nl.ncaj.win9x.ui.theme.controls.ScrollableHost
import nl.ncaj.win9x.ui.theme.controls.ScrollbarPreview
import nl.ncaj.win9x.ui.theme.controls.SpinBoxPreview
import nl.ncaj.win9x.ui.theme.controls.TabsPreview
import nl.ncaj.win9x.ui.theme.controls.Text
import nl.ncaj.win9x.ui.theme.controls.TextBoxPreview
import nl.ncaj.win9x.ui.theme.controls.TreeViewPreview
import nl.ncaj.win9x.ui.theme.controls.Window

@Composable
internal fun ControlsPreview() {
    val verticalScroll = rememberScrollState()
    val horizontalScroll = rememberScrollState()
    Win9xTheme {
        Window(
            title = "Components",
            modifier = Modifier.fillMaxSize(),
            menuBar = {
                entry("Item 1") {
                    label("Sub menu item") {}
                }
                entry("Item 2") {
                    label("Sub menu item") {}
                    divider()
                    cascade("Cascade Item") {
                        label("Command") {}
                        cascade("Sub Cascade Item") {
                            label("Command") {}
                            cascade("Sub Cascade Item") {
                                label("Command") {}
                            }
                        }
                    }
                }
            },
            statusBar = {
                segment(weight = 0.3f) {
                    Text("Status description 1")
                }
                segment(weight = 0.7f) {
                    Text("Status description 2")
                }
            }
        ) {
            ControlsOverview(horizontalScroll, verticalScroll)
        }
    }
}

@Composable
fun ControlsOverview(
    horizontalScroll: ScrollState = rememberScrollState(),
    verticalScroll: ScrollState = rememberScrollState()
) {
    ScrollableHost(
        horizontalScrollState = horizontalScroll,
        verticalScrollState = verticalScroll,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(verticalScroll)
                .widthIn(200.dp)
                .padding(12.dp)
        ) {
            Row {
                ButtonPreview()
                Spacer(modifier = Modifier.width(12.dp))
                OptionSetButtonPreview()
                Spacer(modifier = Modifier.width(12.dp))
                MenuButtonPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                OptionButtonPreview()
                Spacer(modifier = Modifier.width(12.dp))
                CheckboxPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                GroupingPreview()
                Spacer(modifier = Modifier.width(12.dp))
                TextBoxPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                DropDownListBoxPreview()
//                Spacer(modifier = Modifier.width(12.dp))
//                SliderPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                ListBoxPreview()
                Spacer(modifier = Modifier.width(12.dp))
                ScrollbarPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                SpinBoxPreview()
                Spacer(modifier = Modifier.width(12.dp))
                TreeViewPreview()
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                TabsPreview()
                Spacer(modifier = Modifier.width(12.dp))
                ProgressIndicatorPreview()
            }
        }
    }
}