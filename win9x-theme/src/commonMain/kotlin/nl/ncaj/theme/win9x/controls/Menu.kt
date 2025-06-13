package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Node
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequesterModifierNode
import androidx.compose.ui.focus.requestFocus
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import nl.ncaj.theme.win9x.*
import nl.ncaj.theme.win9x.vector.*


@Composable
fun MenuItemCascade(
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val image = if (enabled) rememberVectorPainter(Icons.ArrowDown)
    else rememberVectorPainter(Icons.ArrowDownDisabled)

    val _hover by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered = _hover && LocalInputModeManager.current.inputMode == InputMode.Touch

    MenuItemLabel(
        modifier = modifier,
        interactionSource = interactionSource,
        label = label,
        enabled = enabled,
        selected = selected,
        trailingIcon = {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.rotate(-90f),
                colorFilter = ColorFilter.tint(
                    when {
                        isHovered || isFocused || selected -> Color.White
                        enabled -> Color.Black
                        else -> Color(0xFF808080)
                    }
                ),
            )
        },
        onClick = onClick
    )
}

@Composable
fun MenuItemOptionButton(
    label: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onCheckChanged: (Boolean) -> Unit,
) {
    val _hover by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHover = _hover && LocalInputModeManager.current.inputMode == InputMode.Touch

    MenuItemLabel(
        label = label,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        leadingIcon = {
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.OptionButtonDot),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        when {
                            isHover || isFocused -> Color.White
                            enabled -> Color.Black
                            else -> Color(0xFF808080)
                        }
                    ),
                )
            }
        },
        onClick = { onCheckChanged(!checked) }
    )
}

@Composable
fun MenuItemCheckBox(
    label: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onCheckChanged: (Boolean) -> Unit,
) {
    val _hover by interactionSource.collectIsHoveredAsState()
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHover = _hover && LocalInputModeManager.current.inputMode == InputMode.Touch

    MenuItemLabel(
        label = label,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        leadingIcon = {
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.Checkmark),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        when {
                            isHover || isFocused -> Color.White
                            enabled -> Color.Black
                            else -> Color(0xFF808080)
                        }
                    ),
                )
            }
        },
        onClick = { onCheckChanged(!checked) }
    )
}

@Composable
fun MenuItemLabel(
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    selected: Boolean = false,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .menuItem(interactionSource, selected, onClick)
            .padding(horizontal = 1.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(15.dp),
            contentAlignment = Alignment.Center,
            content = { leadingIcon?.invoke() },
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            interactionSource = interactionSource,
            enabled = enabled,
            selected = selected,
            hoverable = true,
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.size(15.dp),
            contentAlignment = Alignment.Center,
            content = { trailingIcon?.invoke() },
        )
    }
}

internal class MenuState {
    val selectedMenuStack = mutableStateListOf<Any>()
    val subMenuLocations = mutableMapOf<Any, Offset>()

    fun showSubMenu(id: Any) {
        val current = selectedMenuStack.indexOf(id)
        if (current != -1) {
            selectedMenuStack.removeRange(current, selectedMenuStack.size)
        }
        selectedMenuStack.add(id)
    }

    fun hideSubMenuStack(menuId: Any) {
        if (menuId == MenuIdRoot) {
            selectedMenuStack.clear()
            return
        }

        val current = selectedMenuStack.indexOf(menuId)
        if (current != -1) {
            selectedMenuStack.removeRange(current + 1, selectedMenuStack.size)
        }
    }

    fun popMenu() {
        selectedMenuStack.removeLastOrNull()
    }
}

@LayoutScopeMarker
class MenuScope internal constructor(private val state: MenuState) {
    val visibleMenus: List<Any> get() = state.selectedMenuStack

    fun Modifier.cascade(
        menuId: Any,
        interactionSource: MutableInteractionSource? = null,
    ): Modifier = composed {
        val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        LaunchedEffect(isHovered) {
            delay(300)
            if (isHovered) state.showSubMenu(menuId)
        }

        this.hoverable(interactionSource)
            .onPlaced { layoutCoordinates ->
                val positionInParent = layoutCoordinates.positionInParent()
                state.subMenuLocations[menuId] =
                    positionInParent.copy(x = positionInParent.x + layoutCoordinates.size.width)
            }
            .onKeyEvent {
                if (it.type == KeyEventType.KeyUp) {
                    when (it.key) {
                        Key.DirectionRight -> {
                            state.showSubMenu(menuId)
                            true
                        }

                        else -> false
                    }
                } else false
            }
    }
}

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    menu: @Composable MenuScope.(menuId: Any) -> Unit = {},
) {
    val state = remember { MenuState() }
    val scope = MenuScope(state)

    Layout(
        modifier = modifier,
        content = {
            MenuContent(
                modifier = Modifier.menu(MenuIdRoot, state),
                content = { scope.menu(MenuIdRoot) },
            )
            state.selectedMenuStack.forEach { id ->
                MenuContent(
                    modifier = Modifier.menu(id, state).layoutId(id),
                    content = { scope.menu(id) },
                )
            }
        },
        measurePolicy = { measurables, constraints ->
            val rootMenu = measurables.first().measure(constraints)
            val subMenus = if (state.subMenuLocations.isEmpty()) emptyMap()
            else measurables.drop(1).associate { it.layoutId to it.measure(constraints) }

            val subMenusHeight = subMenus.map { state.subMenuLocations[it.key]!!.y.toInt() + it.value.height }.sum()

            layout(
                width = rootMenu.width + subMenus.values.sumOf { it.width },
                height = subMenusHeight + rootMenu.height,
            ) {
                rootMenu.place(0, 0)
                var x = rootMenu.width - 4
                var y = 0

                if (subMenus.isEmpty()) return@layout

                for (id in state.selectedMenuStack) {
                    val subMenu = subMenus[id] ?: continue
                    y += state.subMenuLocations[id]!!.y.toInt()
                    subMenu.place(x, y)
                    x += subMenu.width - 4
                }
            }
        }
    )
}

@Composable
private inline fun MenuContent(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .background(Win9xTheme.colorScheme.buttonFace)
            .windowBorder()
            .padding(Win9xTheme.borderWidthDp),
        content = content
    )
}

private fun Modifier.menuItem(
    interactionSource: MutableInteractionSource,
    selected: Boolean,
    onClick: () -> Unit,
) = this
    .focusSelectionIndication(interactionSource)
    .hoverIndication(interactionSource)
    .then(MenuItemElement(interactionSource))
    .focusable(interactionSource = interactionSource)
    .selectionBackground(selected)
    .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
    )

private class MenuItemElement(
    private val interactionSource: MutableInteractionSource?,
) : ModifierNodeElement<MenuItemNode>() {
    override fun create() = MenuItemNode(interactionSource)

    override fun update(node: MenuItemNode) = node.update(interactionSource)

    override fun InspectorInfo.inspectableProperties() {
        name = "MenuItem"
        properties["enabled"] = true
        properties["interactionSource"] = interactionSource
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MenuItemElement

        return interactionSource == other.interactionSource
    }

    override fun hashCode(): Int {
        return interactionSource.hashCode()
    }
}

data object MenuIdRoot
private data object MenuKey

private class MenuItemNode(
    private var interactionSource: MutableInteractionSource?,
) : KeyInputModifierNode, FocusRequesterModifierNode, PointerInputModifierNode, CompositionLocalConsumerModifierNode,
    Node() {
    private var job: Job? = null
    private var hoverInteraction: HoverInteraction.Enter? = null

    private val inputModeManager get() = currentValueOf(LocalInputModeManager)
    private val focusManager get() = currentValueOf(LocalFocusManager)

    fun update(interactionSource: MutableInteractionSource?) {
        if (this.interactionSource != interactionSource) {
            tryEmitExit()
            this.interactionSource = interactionSource
            job?.cancel()
            interactionSource?.let { listen(it) }
        }
    }

    fun listen(interactionSource: MutableInteractionSource) {
        job = coroutineScope.launch {
            interactionSource.interactions
                .filter { it is HoverInteraction.Enter }
                .collect {
                    requestFocus()
                    traverseAncestors(MenuKey) {
                        (it as MenuNode).onItemHover()
                        false
                    }
                }
        }
    }

    override fun onAttach() {
        super.onAttach()
        interactionSource?.let { listen(it) }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.key == Key.DirectionLeft || event.key == Key.DirectionRight
            || event.key == Key.DirectionUp || event.key == Key.DirectionDown
        ) {
            if(inputModeManager.inputMode == InputMode.Touch) {
                inputModeManager.requestInputMode(InputMode.Keyboard)
                coroutineScope.launch { emitExit() }
            }
        }
        return if (event.type == KeyEventType.KeyUp) {
            when (event.key) {
                Key.DirectionLeft -> {
                    traverseAncestors(MenuKey) {
                        (it as MenuNode).onItemLeftKeyPressed()
                        false
                    }
                    true
                }

                Key.DirectionUp -> {
                    focusManager.moveFocus(FocusDirection.Up)
                    true
                }

                Key.DirectionDown -> {
                    focusManager.moveFocus(FocusDirection.Down)
                    true
                }

                else -> false
            }
        } else false
    }

    override fun onPreKeyEvent(event: KeyEvent) = false

    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize
    ) {
        if (pass == PointerEventPass.Main) {
            when (pointerEvent.type) {
                PointerEventType.Enter -> coroutineScope
                    .launch(start = CoroutineStart.UNDISPATCHED) { emitEnter() }

                PointerEventType.Exit -> coroutineScope
                    .launch(start = CoroutineStart.UNDISPATCHED) { emitExit() }
            }
        }
    }

    override fun onCancelPointerInput() {
        tryEmitExit()
    }

    override fun onDetach() {
        tryEmitExit()
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private suspend fun emitEnter() {
        inputModeManager.requestInputMode(InputMode.Touch)
        if (hoverInteraction == null) {
            val interaction = HoverInteraction.Enter()
            interactionSource?.emit(interaction)
            hoverInteraction = interaction
        }
    }

    private suspend fun emitExit() {
        hoverInteraction?.let { oldValue ->
            val interaction = HoverInteraction.Exit(oldValue)
            interactionSource?.emit(interaction)
            hoverInteraction = null
        }
    }

    private fun tryEmitExit() {
        hoverInteraction?.let { oldValue ->
            val interaction = HoverInteraction.Exit(oldValue)
            interactionSource?.tryEmit(interaction)
            hoverInteraction = null
        }
    }
}

private fun Modifier.menu(id: Any, state: MenuState) =
    this then MenuElement(id, state)

private class MenuElement(
    private val id: Any,
    private val state: MenuState
) : ModifierNodeElement<MenuNode>() {
    override fun create() = MenuNode(id, state)

    override fun update(node: MenuNode) {
        node.update(id, state)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as MenuElement

        if (id != other.id) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }
}

private class MenuNode(private var id: Any, private var state: MenuState) : TraversableNode, Node() {
    override val traverseKey = MenuKey
    private var job: Job? = null

    fun update(id: Any, state: MenuState) {
        this.id = id
        this.state = state
    }

    fun onItemLeftKeyPressed() {
        state.popMenu()
    }

    fun onItemHover() {
        job?.cancel()
        job = coroutineScope.launch {
            delay(300)
            state.hideSubMenuStack(id)
        }
    }
}