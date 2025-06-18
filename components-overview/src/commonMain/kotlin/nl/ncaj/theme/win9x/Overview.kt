package nl.ncaj.theme.win9x

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.compose.resource.ico.IcoImage
import nl.ncaj.compose.resource.ico.IcoResource
import nl.ncaj.theme.win9x.components_overview.generated.resources.Res
import nl.ncaj.theme.win9x.components_overview.generated.resources.ico
import nl.ncaj.theme.win9x.controls.*
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun Overview(
    modifier: Modifier = Modifier,
    itemsPerRow: Int = 3
) {
    val state = rememberLazyListState()
    val verticalScrollbarAdapter = rememberScrollbarAdapter(state)

    val exampleItems = listOf<@Composable () -> Unit>(
        @Composable { ExampleItem("Button") { ButtonExample() } },
        @Composable { ExampleItem("Option set") { OptionSetButtonExample() } },
        @Composable { ExampleItem("Menu button") { MenuButtonExample() } },
        @Composable { ExampleItem("Option button") { OptionButtonExample() } },
        @Composable { ExampleItem("CheckBox") { CheckboxExample() } },
        @Composable { ExampleItem("TextBox") { TextBoxExample() } },
        @Composable { ExampleItem("Slider") { SliderExample() } },
        @Composable { ExampleItem("ListBox") { ListBoxExample() } },
        @Composable { ExampleItem("Scrollbar") { ScrollbarExample() } },
        @Composable { ExampleItem("SpinBox") { SpinBoxExample() } },
        @Composable { ExampleItem("TreeView") { TreeViewExample() } },
        @Composable { ExampleItem("Tabs") { TabsExample() } },
        @Composable { ExampleItem("Progress Indicator") { ProgressIndicatorExample() } },
        @Composable { ExampleItem("Dropdown ListBox") { DropDownListBoxExample() } },
        @Composable { ExampleItem("ComboBox") { ComboBoxExample() } },
        @Composable { ExampleItem("DropDownComboBox") { DropDownComboBoxExample() } },
        @Composable { ExampleItem("ListView") { ListViewExample() } },
    ).chunked(itemsPerRow)

    ScrollableHost(
        modifier = modifier.sunkenBorder(),
        verticalScrollbarAdapter = verticalScrollbarAdapter,
    ) {
        LazyColumn(
            state = state,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            exampleItems.forEach { items ->
                item { Row { items.forEach { item -> item() } } }
            }
        }
    }
}

@Composable
private fun ExampleItem(
    label: String,
    content: @Composable () -> Unit,
) {
    Box(Modifier.size(220.dp).padding(4.dp)) {
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

private data object MenuId1
private data object MenuId2
private data object MenuId3

@Composable
private fun MenuButtonExample() {
    MenuButton(label = { Text("Menu") }) { state ->
        var optionChecked by remember { mutableStateOf(true) }
        var boxChecked by remember { mutableStateOf(true) }
        val selected = state.selectedItem

        MenuItemLabel(
            label = "Command",
            modifier = Modifier.menuItem(key = 0),
            selected = selected == 0,
            onClick = {}
        )
        MenuItemOptionButton(
            label = "Option button",
            modifier = Modifier.menuItem(key = 1),
            selected = selected == 1,
            checked = optionChecked,
            onCheckChanged = { optionChecked = it },
        )
        MenuItemCascade(
            label = "Sub menu1",
            modifier = Modifier.menuItem(key = 2, cascadeMenuId = MenuId1),
            selected = state.visibleMenus.contains(MenuId1) || selected == 2,
        )
        MenuItemCheckBox(
            label = "Checkbox",
            modifier = Modifier.menuItem(key = 3),
            selected = selected == 3,
            checked = boxChecked,
            onCheckChanged = { boxChecked = it },
        )
        MenuItemCascade(
            label = "Sub menu2",
            modifier = Modifier.menuItem(key = 4, cascadeMenuId = MenuId2),
            selected = state.visibleMenus.contains(MenuId2) || selected == 4,
        )

        cascadeMenu(MenuId1) {
            MenuItemLabel(
                label = "Sub command 1",
                modifier = Modifier.menuItem(key = 5),
                selected = selected == 5,
                onClick = {},
            )
            MenuItemCascade(
                label = "Sub menu3",
                modifier = Modifier.menuItem(key = 6, cascadeMenuId = MenuId3),
                selected = state.visibleMenus.contains(MenuId3) || selected == 6,
            )

            cascadeMenu(MenuId3) {
                MenuItemLabel(
                    label = "Sub command 3",
                    modifier = Modifier.menuItem(key = 8),
                    selected = selected == 8,
                    onClick = {},
                )
            }
        }

        cascadeMenu(MenuId2) {
            MenuItemLabel(
                label = "Sub command 2",
                modifier = Modifier.menuItem(key = 7),
                selected = selected == 7,
                onClick = {},
            )
        }
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
        TextBox(text, onValueChange = { text = it }, maxLines = 1)
        Spacer(modifier = Modifier.height(2.dp))
        TextBox("Disabled", onValueChange = {}, enabled = false)
    }
}

@Composable
private fun SliderExample() {
    Slider(steps = 4, onStep = { })
}

@Composable
private fun ListBoxExample() {
    var state = rememberListBoxState(itemCount = 5)
    ListBox(
        state = state,
        modifier = Modifier.fillMaxSize(0.6f),
    ) { index ->
        val interactionSource = remember { MutableInteractionSource() }
        Text(
            modifier = Modifier
                .listBoxItem(enabled = index != 2, interactionSource)
                .fillMaxWidth()
                .padding(4.dp),
            text = "Value ${index + 1}",
            enabled = index != 2,
            selected = state.selectedIndex == index,
        )
    }
}

@Composable
private fun ScrollbarExample() {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScrollState)
    val verticalScrollbarAdapter = rememberScrollbarAdapter(verticalScrollState)

    ScrollableHost(
        modifier = Modifier.sunkenBorder(),
        horizontalScrollbarAdapter = horizontalScrollbarAdapter,
        verticalScrollbarAdapter = verticalScrollbarAdapter,
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
    var collapsable by remember { mutableStateOf(true) }
    var showRelationship by remember { mutableStateOf(true) }

    fun TreeViewScope.labelItem(
        label: String,
        enabled: Boolean = true,
        children: (TreeViewScope.() -> Unit)? = null
    ) = item(label, children) {
        val interactionSource = remember { MutableInteractionSource() }
        TreeViewItem(
            label = label,
            enabled = enabled,
            interactionSource = interactionSource,
            leadingIcon = {
                IcoImage(
                    resource = Res.ico.directory_open,
                    contentDescription = null,
                    modifier = Modifier.focusable(interactionSource = interactionSource),
                    colorFilter = Win9xOverlayColorFilter(interactionSource)
                )
            },
            onClick = { },
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
                        labelItem("Value 3.2.1.3")
                    }
                    labelItem("Value 3.2.2") {
                        labelItem("Value 3.2.2.1")
                        labelItem("Value 3.2.2.2") {
                            labelItem("Value 3.2.2.2.1") {
                                labelItem("Value 3.2.2.2.1.1")
                            }
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
        tabCount = 3,
        tabs = { Text("Tab ${it + 1}") },
        modifier = Modifier.fillMaxSize(),
        content = {
            Text(
                text = "Tab selected: ${selectedTabIndex + 1}",
                modifier = Modifier.fillMaxSize()
            )
        },
    )
}

@Composable
private fun ProgressIndicatorExample() {
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            repeatMode = RepeatMode.Reverse,
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        )
    )
    ProgressIndicator(progress)
}

@Composable
private fun DropDownListBoxExample() {
    var expanded by remember { mutableStateOf(false) }
    var currentValue by remember { mutableStateOf("Value") }
    var selectedItem by remember { mutableIntStateOf(0) }
    DropDownListBox(
        text = currentValue,
        expanded = expanded,
        onExpandChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        DropDownItem(
            text = "Value",
            selected = selectedItem == 0,
            onSelectionChange = { if (it) selectedItem = 0 },
            onClick = {
                currentValue = "Value"
                expanded = false
            }
        )
        DropDownItem(
            text = "Value (disabled)",
            selected = false,
            onSelectionChange = {},
            onClick = {},
            enabled = false
        )
        DropDownItem(
            text = "Longer value",
            selected = selectedItem == 1,
            onSelectionChange = { if (it) selectedItem = 1 },
            onClick = {
                currentValue = "Longer value"
                expanded = false
            }
        )
    }
}

@Composable
private fun ComboBoxExample() {
    var value by remember { mutableStateOf("") }
    var state = rememberListBoxState(itemCount = 3, defaultIndex = -1) { index -> value = "Value ${index + 1}" }
    Column(Modifier.fillMaxSize(0.6f)) {
        TextBox(
            value = value,
            onValueChange = { value = it },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        ListBox(
            state = state,
            modifier = Modifier.fillMaxSize(),
        ) { index ->
            Text(
                modifier = Modifier
                    .listBoxItem(enabled = index != 1)
                    .fillMaxWidth()
                    .padding(4.dp),
                text = "Value ${index + 1}",
                enabled = index != 1,
                selected = state.selectedIndex == index,
            )
        }
    }
}

@Composable
private fun DropDownComboBoxExample() {
    var value by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(0) }

    DropDownComboBox(
        value = value,
        onValueChange = { value = it },
        expanded = expanded,
        onExpandChange = { expanded = it },
    ) {
        for (index in 0 until 3) {
            val label = "Value ${index + 1}"
            DropDownItem(
                text = label,
                enabled = index != 1,
                selected = index != 1 && selected == index,
                onSelectionChange = { selected = index },
                onClick = {
                    value = "Value ${index + 1}"
                    expanded = false
                }
            )
        }
    }
}

enum class ListViewState { LargeIcon, SmallIcon, List, Details }
class ListViewItem(val label: String, val description: String, val data: String, val icon: IcoResource)

val listViewItems = (0 until 20)
    .map { ListViewItem("Item $it", "Description", "data", Res.ico.directory_open) }

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ListViewExample() {
    var selectedViewState by remember { mutableStateOf(ListViewState.LargeIcon) }

    Column {
        when (selectedViewState) {
            ListViewState.LargeIcon -> VerticalListView(
                modifier = Modifier.weight(1f)
            ) {
                listViewItems.forEach {
                    val interactionSource = remember { MutableInteractionSource() }
                    LargeIconListItem(
                        label = it.label,
                        interactionSource = interactionSource,
                        icon = {
                            IcoImage(
                                resource = it.icon,
                                contentDescription = null,
                                modifier = Modifier.focusable(interactionSource = interactionSource),
                                colorFilter = Win9xOverlayColorFilter(interactionSource)
                            )
                        },
                    )
                }
            }

            ListViewState.SmallIcon -> VerticalListView(
                modifier = Modifier.weight(1f)
            ) {
                listViewItems.forEach {
                    val interactionSource = remember { MutableInteractionSource() }
                    SmallIconListItem(
                        label = it.label,
                        interactionSource = interactionSource,
                        icon = {
                            IcoImage(
                                resource = it.icon,
                                contentDescription = null,
                                modifier = Modifier.focusable(interactionSource = interactionSource),
                                colorFilter = Win9xOverlayColorFilter(interactionSource)
                            )
                        },
                    )
                }
            }

            ListViewState.List -> HorizontalListView(
                modifier = Modifier.weight(1f)
            ) {
                listViewItems.forEach {
                    val interactionSource = remember { MutableInteractionSource() }
                    SmallIconListItem(
                        label = it.label,
                        interactionSource = interactionSource,
                        icon = {
                            IcoImage(
                                resource = it.icon,
                                contentDescription = null,
                                modifier = Modifier.focusable(interactionSource = interactionSource),
                                colorFilter = Win9xOverlayColorFilter(interactionSource)
                            )
                        },
                    )
                }
            }

            ListViewState.Details -> DetailsListView(
                columns = 3,
                modifier = Modifier.weight(1f)
            ) {
                headingRow { column ->
                    val label = when (column) {
                        0 -> "Title"
                        1 -> "Description"
                        2 -> "Data"
                        else -> error("Column label not found")
                    }
                    ColumnHeading(
                        label = label,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                listViewItems.forEach {
                    itemRow { column ->
                        val interactionSource = remember { MutableInteractionSource() }
                        when (column) {
                            0 -> DetailsViewListItem(
                                label = it.label,
                                interactionSource = interactionSource,
                                icon = {
                                    IcoImage(
                                        resource = it.icon,
                                        contentDescription = null,
                                        modifier = Modifier.focusable(interactionSource = interactionSource),
                                        colorFilter = Win9xOverlayColorFilter(interactionSource)
                                    )
                                },
                                selectable = true,
                            )

                            1 -> DetailsViewListItem(label = it.description)
                            2 -> DetailsViewListItem(label = it.data)
                            else -> error("Column data not found")
                        }
                    }
                }
            }
        }
        FlowRow(
            modifier = Modifier.padding(top = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            ListViewState.entries.forEachIndexed { index, viewState ->
                OptionButton(
                    checked = viewState == selectedViewState,
                    onCheckChange = { selectedViewState = viewState },
                    label = { Text(viewState.name) },
                    modifier = if (index == ListViewState.entries.size - 1) Modifier
                    else Modifier.padding(end = 4.dp)
                )
            }
        }
    }
}
