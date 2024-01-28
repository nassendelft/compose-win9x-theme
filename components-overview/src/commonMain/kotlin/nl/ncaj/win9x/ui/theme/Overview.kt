package nl.ncaj.win9x.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.controls.Button
import nl.ncaj.win9x.ui.theme.controls.Checkbox
import nl.ncaj.win9x.ui.theme.controls.DropDownListBox
import nl.ncaj.win9x.ui.theme.controls.DropDownListBoxItem
import nl.ncaj.win9x.ui.theme.controls.Grouping
import nl.ncaj.win9x.ui.theme.controls.ListBox
import nl.ncaj.win9x.ui.theme.controls.MenuButton
import nl.ncaj.win9x.ui.theme.controls.OptionButton
import nl.ncaj.win9x.ui.theme.controls.OptionSetButton
import nl.ncaj.win9x.ui.theme.controls.ProgressIndicator
import nl.ncaj.win9x.ui.theme.controls.ScrollableHost
import nl.ncaj.win9x.ui.theme.controls.Slider
import nl.ncaj.win9x.ui.theme.controls.SpinBox
import nl.ncaj.win9x.ui.theme.controls.TabHost
import nl.ncaj.win9x.ui.theme.controls.Text
import nl.ncaj.win9x.ui.theme.controls.TextBox
import nl.ncaj.win9x.ui.theme.controls.TreeView
import nl.ncaj.win9x.ui.theme.controls.TreeViewItem
import nl.ncaj.win9x.ui.theme.controls.rememberScrollbarState
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.LoadState
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.resource

@Composable
fun Overview(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(200.dp),
        modifier = modifier
    ) {
        item { ExampleItem("Button") { ButtonExample() } }
        item { ExampleItem("Option set") { OptionSetButtonExample() } }
        item { ExampleItem("Menu button") { MenuButtonExample() } }
        item { ExampleItem("Option button") { OptionButtonExample() } }
        item { ExampleItem("CheckBox") { CheckboxExample() } }
        item { ExampleItem("TextBox") { TextBoxExample() } }
        item { ExampleItem("Slider") { SliderExample() } }
        item { ExampleItem("ListBox") { ListBoxExample() } }
        item { ExampleItem("Scrollbar") { ScrollbarExample() } }
        item { ExampleItem("SpinBox") { SpinBoxExample() } }
        item { ExampleItem("TreeView") { TreeViewExample() } }
        item { ExampleItem("Tabs") { TabsExample() } }
        item { ExampleItem("Progress Indicator") { ProgressIndicatorExample() } }
        item { ExampleItem("Dropdown ListBox") { DropDownListBoxExample() } }
        item { ExampleItem("ComboBox") { ComboBoxExample() } }
    }
}

@Composable
private fun ExampleItem(
    label: String,
    content: @Composable () -> Unit,
) {
    Box(Modifier.size(200.dp).padding(4.dp)) {
        Grouping(
            label = label,
            modifier = Modifier.fillMaxSize(),
            labelAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.matchParentSize(),
                content = { content() }
            )
        }
    }
}

@Composable
private fun ButtonExample() {
    Column {
        Button(
            onClick = {},
        ) { Text("Default") }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {},
            enabled = false
        ) { Text("Disabled", enabled = false) }
    }
}

@Composable
private fun OptionSetButtonExample() {
    var set by remember { mutableStateOf(false) }
    Column {
        OptionSetButton(set = set, { set = it }) {
            Text(if (set) "Set" else "Not set")
        }
        Spacer(modifier = Modifier.height(2.dp))
        OptionSetButton(set = true, {}, enabled = false) {
            Text("Set Disabled", enabled = false)
        }
    }
}

@Composable
private fun MenuButtonExample() {
    MenuButton(
        menu = {
            label("Command") {}
            divider()
            cascade("Check Box") {
                label("Command") {}
            }
        }
    ) {
        Text("Menu")
    }
}

@Composable
private fun OptionButtonExample() {
    var checked by remember { mutableStateOf(true) }
    Column {
        OptionButton(checked = checked, onCheckChange = { checked = it }) {
            Text("Default")
        }
        OptionButton(checked = false, onCheckChange = {}, enabled = false) {
            Text("Disabled", enabled = false)
        }
        OptionButton(checked = true, onCheckChange = {}, enabled = false) {
            Text("Disabled checked", enabled = false)
        }
    }
}

@Composable
private fun CheckboxExample() {
    var checked by remember { mutableStateOf(true) }
    Column {
        Checkbox(checked = checked, onCheckChange = { checked = it }) {
            Text("Default")
        }
        Checkbox(checked = false, onCheckChange = {}, enabled = false) {
            Text("Disabled", enabled = false)
        }
        Checkbox(checked = true, onCheckChange = {}, enabled = false) {
            Text("Disabled checked", enabled = false)
        }
    }
}

@Composable
private fun TextBoxExample() {
    var text by remember { mutableStateOf("Default") }
    Column {
        TextBox(text, onValueChange = { text = it }, maxLines = 4)
        Spacer(modifier = Modifier.height(2.dp))
        TextBox("Disabled", onValueChange = {}, enabled = false)
    }
}

@Composable
private fun SliderExample() {
    Slider(modifier = Modifier, steps = 4, onStep = { })
}

@Composable
private fun ListBoxExample() {
    var selection by remember { mutableIntStateOf(0) }

    ListBox(
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        item {
            DropDownListBoxItem(
                label = "Value 1",
                onSelected = { selection = 0 },
                selected = selection == 0
            )
        }
        item {
            DropDownListBoxItem(
                label = "Value 2 (Disabled)",
                enabled = false,
                onSelected = { selection = 1 },
                selected = selection == 1
            )
        }
        item {
            DropDownListBoxItem(
                label = "Value 3",
                onSelected = { selection = 3 },
                selected = selection == 3
            )
        }
    }
}

@Composable
private fun ScrollbarExample() {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    val scrollbarAdapter = rememberScrollbarState(horizontalScrollState, verticalScrollState)

    ScrollableHost(
        modifier = Modifier
            .sunkenBorder()
            .padding(Win9xTheme.borderWidthDp),
        scrollbarState = scrollbarAdapter,
    ) {
        Text(
            text = "Some text that is repeated multiple times\n".repeat(15),
            modifier = Modifier
                .background(Win9xTheme.colorScheme.buttonHighlight)
                .padding(4.dp)
                .horizontalScroll(horizontalScrollState)
                .verticalScroll(verticalScrollState)
        )
    }
}

@Composable
private fun SpinBoxExample() {
    val decimalRegex = """[^0-9]""".toRegex()
    var value by remember { mutableIntStateOf(1) }

    SpinBox(
        value.toString(),
        onValueChange = {
            value = decimalRegex.replace(it, "").takeIf { it.isNotBlank() }?.toInt() ?: 0
        },
        onIncrease = { value++ },
        onDecrease = { value-- },
    )
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TreeViewExample() {
    val state = resource("png_images/directory_open.png").rememberImageBitmap()
    val icon = if (state is LoadState.Success<ImageBitmap>) {
        remember(state.value) { BitmapPainter(state.value) }
    } else null
    var collapsable by remember { mutableStateOf(true) }
    var showRelationship by remember { mutableStateOf(true) }

    @Composable
    fun Item(label: String, enabled: Boolean = true) {
        TreeViewItem(
            label = label,
            enabled = enabled,
            leadingIcon = { icon?.let { Image(it, contentDescription = "") } },
            onClick = {}
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TreeView(
            collapsable = collapsable,
            showRelationship = showRelationship,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            item { Item("Value 1") }
            item { Item("Value 2", enabled = false) }
            item(
                content = { Item("Value 3") },
                children = {
                    item { Item("Value 3.1") }
                    item(
                        content = { Item("Value 3.2") },
                        children = {
                            item(
                                content = { Item("Value 3.2.1") },
                                children = {
                                    item { Item("Value 3.2.1.1") }
                                    item(
                                        content = { Item("Value 3.2.1.2") },
                                        children = {
                                            item { Item("Value 3.2.1.2.1") }
                                            item { Item("Value 3.2.1.2.2") }
                                        }
                                    )
                                    item { Item("Value 3.2.1.2") }
                                }
                            )
                            item(
                                content = { Item("Value 3.2.2") },
                                children = {
                                    item { Item("Value 3.2.2.1") }
                                    item(
                                        content = { Item("Value 3.2.2.2") },
                                        children = {
                                            item { Item("Value 3.2.2.2.1") }
                                        }
                                    )
                                }
                            )
                        }
                    )
                    item { Item("Value 3.3") }
                    item(
                        content = { Item("Value 3.4") },
                        children = {
                            item { Item("Value 3.4.1") }
                        }
                    )
                }
            )
        }
        Row(
            Modifier.padding(top = 4.dp)
        ) {
            Checkbox(
                checked = collapsable,
                onCheckChange = { collapsable = it },
                label = { Text("Collapsable") }
            )
            Spacer(Modifier.width(4.dp))
            Checkbox(
                checked = showRelationship,
                onCheckChange = { showRelationship = it },
                label = { Text("Relationship") }
            )
        }
    }
}

@Composable
private fun TabsExample() {
    var selectedTabIndex by remember { mutableIntStateOf(1) }

    TabHost(
        selectedTabIndex = selectedTabIndex,
        onTabSelected = { selectedTabIndex = it },
        tabs = { for (i in 0..2) tab { Text("Tab ${i + 1}") } },
        modifier = Modifier.fillMaxSize(),
        content = {
            Text(
                text = "Tab selected: ${selectedTabIndex + 1}",
                modifier = Modifier.widthIn(150.dp)
            )
        },
    )
}

@Composable
private fun ProgressIndicatorExample() {
    ProgressIndicator()
}

@Composable
private fun DropDownListBoxExample() {
    var expanded by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf("Value") }
    DropDownListBox(
        text = currentValue,
        expanded = expanded,
        onExpandChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        item { DropDownListBoxItem(text = "Value", onSelection = { currentValue = "Value" }) }
        item { DropDownListBoxItem(text = "Value (disabled)", onSelection = { }, enabled = false) }
        item {
            DropDownListBoxItem(
                text = "Longer value",
                onSelection = { currentValue = "Longer value" })
        }
    }
}

@Composable
private fun ComboBoxExample() {
    var value by remember { mutableStateOf("") }
    Column(Modifier.width(IntrinsicSize.Max)) {
        TextBox(value = value, onValueChange = { value = it })
        ListBox(Modifier.fillMaxWidth()) {
            listOf("Value 1", "Value 2", "Value 3").forEach {
                item {
                    DropDownListBoxItem(
                        label = it,
                        onSelected = { value = it },
                        selected = value == it
                    )
                }
            }
        }
    }
}