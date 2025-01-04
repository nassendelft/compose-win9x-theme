package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Node
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.traverseAncestors
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.hoverSelection
import nl.ncaj.theme.win9x.vector.*
import nl.ncaj.theme.win9x.windowBorder


@Composable
fun MenuItemCascade(
    label: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    val image = if (enabled) rememberVectorPainter(Icons.ArrowDown)
    else rememberVectorPainter(Icons.ArrowDownDisabled)

    val isHovered by interactionSource.collectIsHoveredAsState()

    MenuItemLabel(
        modifier = modifier,
        interactionSource = interactionSource,
        label = label,
        enabled = enabled,
        trailingIcon = {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.rotate(-90f),
                colorFilter = ColorFilter.tint(
                    when {
                        isHovered -> Color.White
                        enabled -> Color.Black
                        else -> Color(0xFF808080)
                    }
                ),
            )
        },
        onClick = {}
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
    val isHover by interactionSource.collectIsHoveredAsState()

    MenuItemLabel(
        label = label,
        modifier = modifier.hoverable(interactionSource),
        enabled = enabled,
        leadingIcon = {
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.OptionButtonDot),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        when {
                            isHover -> Color.White
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
    val isHover by interactionSource.collectIsHoveredAsState()

    MenuItemLabel(
        label = label,
        modifier = modifier.hoverable(interactionSource),
        enabled = enabled,
        leadingIcon = {
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.Checkmark),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(
                        when {
                            isHover -> Color.White
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
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    val isHover by interactionSource.collectIsHoveredAsState()

    MenuItem(
        modifier = modifier.clickable(interactionSource = interactionSource, indication = null) { onClick?.invoke() },
        interactionSource = interactionSource,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(15.dp),
            contentAlignment = Alignment.Center,
            content = { leadingIcon?.invoke() },
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            enabled = enabled,
            color = if (isHover) ColorProducer { Color.White } else null
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier.size(15.dp),
            contentAlignment = Alignment.Center,
            content = { trailingIcon?.invoke() },
        )
    }
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .padding(Win9xTheme.borderWidthDp)
            .menuItem(interactionSource)
            .padding(horizontal = 1.dp, vertical = 3.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content
    )
}

class MenuState internal constructor() {
    val selectedMenuStack = mutableStateListOf<Any>()

    fun showSubMenu(id: Any) {
        val current = selectedMenuStack.indexOf(id)
        if (current != -1) {
            selectedMenuStack.removeRange(current, selectedMenuStack.size)
        }
        selectedMenuStack.add(id)
    }

    fun hideSubMenu(menuId: Any) {
        if (menuId == MenuIdRoot) {
            selectedMenuStack.clear()
            return
        }
        val current = selectedMenuStack.indexOf(menuId)
        if (current != -1) {
            selectedMenuStack.removeRange(current + 1, selectedMenuStack.size)
        }
    }

    internal val subMenuLocations = mutableMapOf<Any, Offset>()
}

@LayoutScopeMarker
class MenuScope internal constructor(private val state: MenuState) {
    fun Modifier.cascade(menuId: Any): Modifier = composed {
        val interactionSource = remember { MutableInteractionSource() }
        val isHovered by interactionSource.collectIsHoveredAsState()

        LaunchedEffect(isHovered) {
            delay(500)
            if (isHovered) state.showSubMenu(menuId)
        }

        this.hoverable(interactionSource).onPlaced { layoutCoordinates ->
            val positionInParent = layoutCoordinates.positionInParent()
            state.subMenuLocations[menuId] =
                positionInParent.copy(x = positionInParent.x + layoutCoordinates.size.width)
        }
    }
}

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit = {},
    content: @Composable MenuScope.() -> Unit
) {
    val state = remember { MenuState() }
    val scope = MenuScope(state)

    Layout(
        modifier = modifier,
        content = {
            MenuContent(Modifier.menu(MenuIdRoot, state)) { scope.content() }
            state.selectedMenuStack.forEach { id ->
                MenuContent(modifier = Modifier.menu(id, state), content = { scope.subMenu(id) })
            }
        },
        measurePolicy = { measurables, constraints ->
            println(constraints)
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
            .windowBorder(),
        content = { content() }
    )
}

private fun Modifier.menuItem(interactionSource: MutableInteractionSource) =
    this.hoverSelection(interactionSource)
        .then(MenuItemElement(interactionSource))

private class MenuItemElement(
    private val interactionSource: MutableInteractionSource?
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

private data object MenuIdRoot
private data object MenuKey

private class MenuItemNode(var interactionSource: MutableInteractionSource?) : Node() {
    private var job: Job? = null

    fun update(interactionSource: MutableInteractionSource?) {
        this.interactionSource = interactionSource
        job?.cancel()
        interactionSource?.let { listen(it) }
    }

    @OptIn(FlowPreview::class)
    fun listen(interactionSource: MutableInteractionSource) {
        job = coroutineScope.launch {
            interactionSource.interactions
                .filter { it is HoverInteraction.Enter }
                .debounce(300)
                .collect {
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
}

private fun Modifier.menu(id: Any, state: MenuState) =
    this.layoutId(id) then MenuElement(id, state)

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

    fun update(id: Any, state: MenuState) {
        this.id = id
        this.state = state
    }

    fun onItemHover() = state.hideSubMenu(id)
}