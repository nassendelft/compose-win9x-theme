# Win9x Theme for Jetpack Compose UI

This is a Win9x Theme library for Jetpack Compose UI. This is a unique, self-contained theme designed specifically for
Jetpack Compose UI. Unlike standard Material Design themes, this project offers a complete set of custom components, all
built directly from Jetpack Compose primitives.

## Key Features

- Standalone Components: Each component is designed independently, without relying on the Material Design theme. This
  gives you extensive customization options.
- Multiplatform Compatibility: The theme supports JVM, Android, and JSWasm targets, making it versatile for various
  platforms. While the primary focus is currently on the JVM target, iOS compatibility is planned for the future.
- Development Stage: This project is currently in a pre-release stage. This means you might encounter breaking changes
  before we reach version 1.0.0. We encourage early adopters to experiment with it and provide valuable feedback.

## Live demo

You can see the `win9x-theme` library in action by visiting our live JSWasm demo at http://win9x-compose.ncaj.nl/.
Please note that this JSWasm demo is experimental and doesn't fully represent all the capabilities of the JVM target.

## Screenshots

<table>
  <tr>
    <td >JS Wasm</td>
    <td >JVM</td>
  </tr>
  <tr>
    <td ><img src="screenshots/components_overview_jswasm.png" alt="win9x theme components for jsWasm" width="400"/></td>
    <td ><img src="screenshots/components_overview_jvm.png" alt="win9x theme components for jvm" width="400"/></td>
  </tr>
  <tr>
    <td >Android</td>
  </tr>
  <tr>
    <td ><img src="screenshots/components_overview_android.png" alt="win9x theme components for android" width="200"/></td>
  </tr>
</table>

## Getting Started

Integration into a Jetpack Compose project is straightforward:

### Step 1: Adding the Repository

This library is published to both GitHub Packages and Maven Central.

#### Using GitHub packages

Make sure you
configure [authentication properly](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry#authenticating-to-github-packages).

```kotlin
repositories {
    maven(url = "https://maven.pkg.github.com/nassendelft/compose-win9x-theme")
}
```

#### Using Maven Central packages

```kotlin
repositories {
    mavenCentral()
}
```

### Step 2: Adding the Dependency

Add the `win9x-theme` dependency to your project's `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("nl.ncaj.theme.win9x:win9x-theme:VERSION")
}
```

### Step 3: Sync Project

After adding the repository and dependency, sync your project with the Gradle files. This will
download the library and make it available for use in your project.

### Step 4: Importing and Using the Library

Now, you can import and use the `win9x-theme` library in your Kotlin code:

```kotlin
fun main() = application {
    Win9xTheme {
        Window(
            title = "Win9x theme for Jetpack Compose",
            onCloseRequested = ::exitApplication,
        ) {
            // your content
        }
    }
}
```

## Available components

### Button

The Button control represents a clickable button that triggers an action when interacted with.

Usage example:

```kotlin
Button(onClick = { /* Handle click */ }) {
    Text("Click Me")
}
```

### Option Set

An OptionSet is a collection of mutually exclusive options, rendered as radio buttons

Usage example:

```kotlin
var set by remember { mutableStateOf(false) }
OptionSetButton(set, onSetChanged = { set = it })
```

### Menu Button

A MenuButton displays a menu when clicked, allowing users to select from multiple options.

Usage example:

```kotlin
private data object MenuId1
MenuButton(
    label = "Menu",
    menu = {
        val selected = state.selectedItem
        MenuItemLabel(
            label = "Command",
            modifier = Modifier.menuItem(key = 0),
            selected = selected == 0,
            onClick = {}
        )
        MenuItemDivider()
        MenuItemCascade(
            label = "Sub menu=",
            modifier = Modifier.menuItem(key = 1, cascadeMenuId = MenuId1),
            selected = state.visibleMenus.contains(MenuId1) || selected == 1,
        )
        cascadeMenu(MenuId1) {
            MenuItemLabel(
                label = "Sub command",
                modifier = Modifier.menuItem(key = 2),
                selected = selected == 2,
                onClick = {},
            )
        }
    }
)
```

### Option Button

An OptionButton is a toggleable button that can represent a binary choice, such as on/off states.

Usage example:

```kotlin
var checked by remember { mutableStateOf(true) }
OptionButton(checked = checked, onCheckChange = { checked = it }) {
    Text("Default")
}
```

### CheckBox

A CheckBox allows users to select one or more items from a set.

Usage example:

```kotlin
var checked by remember { mutableStateOf(true) }
Checkbox(checked = checked, onCheckChange = { checked = it }) {
    Text("Default")
}
```

### TextBox

A TextBox is an input field where users can enter text.

Usage example:

```kotlin
var text by remember { mutableStateOf("Default") }
TextBox(text, onValueChange = { text = it })
```

### Slider

A Slider is a control that lets users select a value from a continuous range by moving a slider
thumb.

Usage example:

```kotlin
Slider(steps = 4, onStep = { })
```

### ListBox

A ListBox presents a list of selectable items, often used for dropdown menus or lists of options.

Usage example:

```kotlin
val state = rememberListBoxState(itemCount = 5)
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
```

### Scrollbar

A Scrollbar provides a visual indication of the scrollable content's position and size relative to
the viewport.

Usage example:

```kotlin
val scrollState = rememberScrollState()
Box(Modifier.size(50.dp)) {
    Text(
        text = "Some text that is repeated multiple times\n".repeat(15),
        modifier = Modifier.verticalScroll(scrollState)
    )
    VerticalScrollBar(scrollState = scrollState)
}
```

### SpinBox

A SpinBox is an input control that allows users to increase or decrease a value using up and
down arrows.

Usage example:

```kotlin
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
```

### TreeView

A TreeView displays hierarchical data in a tree structure, with expandable and collapsible nodes.

Usage example:

```kotlin
TreeView(
    collapsable = true,
    showRelationship = true,
) {
    item { Item("Value 1") }
    item { Item("Value 2", enabled = false) }
    item(
        content = { Item("Value 3") },
        children = {
            item { Item("Value 3.1") }
            item { Item("Value 3.2") }
        }
    )
}
```

### Tabs

Tabs organize content into separate views which users can switch between.

Usage example:

```kotlin
var selectedTabIndex by remember { mutableIntStateOf(1) }
TabHost(
    selectedTabIndex = selectedTabIndex,
    onTabSelected = { selectedTabIndex = it },
    tabs = { for (i in 0..2) tab { Text("Tab ${i + 1}") } },
    content = {
        Text(text = "Tab selected: ${selectedTabIndex + 1}")
    },
)
```

### Progress Indicator

A ProgressIndicator visually indicates the progress of a long-running operation.

Usage example:

```kotlin
ProgressIndicator(progress = 0.5f)
```

### Dropdown ListBox

A DropdownListBox is a list box that appears as a dropdown menu when activated.

Usage example:

```kotlin
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
```

### ComboBox

A ComboBox combines a text box with a drop-down list, allowing users to either type a value directly
or choose from the list.

Usage example:

```kotlin
var value by remember { mutableStateOf("") }
val state = rememberListBoxState(itemCount = 3, defaultIndex = -1) { index -> value = "Value ${index + 1}" }
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
```

### DropDownComboBox

A DropDownComboBox is similar to a ComboBox, but it always shows the drop-down list, even when not
focused.

Usage example:

```kotlin
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
```

For more detailed information and examples, please refer to the source code of the win9x-theme library.

## Contributions

Contributions are welcomed! To share ideas, report bugs, or propose
enhancements, simply open an issue or contribute via a pull request.

## Licensing

The project is governed by the [GPLv3 License](LICENSE).
