package nl.ncaj.theme.win9x

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.controls.*
import org.jetbrains.compose.resources.painterResource
import win9x.components_overview.generated.resources.Res
import win9x.components_overview.generated.resources.directory_open

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
        item { ExampleItem("DropDownComboBox") { DropDownComboBoxExample() } }
    }
}

@Composable
private fun ExampleItem(
    label: String,
    content: @Composable () -> Unit,
) {
    Box(Modifier.size(400.dp).padding(4.dp)) {
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
        modifier = Modifier.fillMaxSize(0.6f)
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

@Composable
private fun TreeViewExample() {
    var collapsable by remember { mutableStateOf(true) }
    var showRelationship by remember { mutableStateOf(true) }

    fun TreeViewScope.labelItem(
        label: String,
        enabled: Boolean = true,
        children: (TreeViewScope.() -> Unit)? = null
    ) = item(children) {
        TreeViewItem(
            label = label,
            enabled = enabled,
            leadingIcon = { Image(painterResource(Res.drawable.directory_open), contentDescription = "") },
            onClick = {}
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TreeView(
            collapsable = collapsable,
            showRelationship = showRelationship,
            modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
            labelItem("Value 1")
            labelItem("Value 2", enabled = false)
            labelItem("Value 3") {
                labelItem("Value 3.1")
                labelItem("Value 3.2") {
                    labelItem("Value 3.2.1") {
                        labelItem("Value 3.2.1.1")
                        labelItem("Value 3.2.1.2") {
                            labelItem("Value 3.2.1.2.1")
                            labelItem("Value 3.2.1.2.2")
                        }
                        labelItem("Value 3.2.1.2")
                    }
                    labelItem("Value 3.2.2") {
                        labelItem("Value 3.2.2.1")
                        labelItem("Value 3.2.2.2") {
                            labelItem("Value 3.2.2.2.1")
                        }
                    }
                }
                labelItem("Value 3.3")
                labelItem("Value 3.4") {
                    labelItem("Value 3.4.1")
                }
            }
        }
        Row(
            modifier = Modifier.padding(top = 4.dp)
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
    ProgressIndicator(progress = 0.5f)
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
    Column(Modifier.fillMaxSize(0.6f)) {
        TextBox(value = value, onValueChange = { value = it }, modifier = Modifier.fillMaxWidth())
        ListBox(Modifier.fillMaxSize()) {
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

@Composable
private fun DropDownComboBoxExample() {
    var value by remember { mutableStateOf("") }
    DropDownComboBox(
        value = value,
        onValueChange = { value = it }
    ) {
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