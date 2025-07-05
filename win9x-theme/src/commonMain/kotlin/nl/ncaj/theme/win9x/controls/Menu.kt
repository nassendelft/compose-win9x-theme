package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.onHover
import nl.ncaj.theme.win9x.selectionBackground
import nl.ncaj.theme.win9x.vector.*
import nl.ncaj.theme.win9x.windowBorder


@Composable
fun MenuItemCascade(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    val image = if (enabled) rememberVectorPainter(Icons.ArrowDown)
    else rememberVectorPainter(Icons.ArrowDownDisabled)

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
                        selected -> Color.White
                        enabled -> Color.Black
                        else -> Color(0xFF808080)
                    }
                ),
            )
        },
        selected = selected,
        onClick = onClick
    )
}

@Composable
fun MenuItemOptionButton(
    label: String,
    selected: Boolean,
    checked: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onCheckChanged: (Boolean) -> Unit,
) {
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
                            selected -> Color.White
                            enabled -> Color.Black
                            else -> Color(0xFF808080)
                        }
                    ),
                )
            }
        },
        selected = selected,
        onClick = { onCheckChanged(!checked) }
    )
}

@Composable
fun MenuItemCheckBox(
    label: String,
    selected: Boolean,
    checked: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onCheckChanged: (Boolean) -> Unit,
) {
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
                            selected -> Color.White
                            enabled -> Color.Black
                            else -> Color(0xFF808080)
                        }
                    ),
                )
            }
        },
        selected = selected,
        onClick = { onCheckChanged(!checked) }
    )
}

@Composable
fun MenuItemLabel(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
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
            enabled = enabled,
            selected = selected,
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
fun MenuItemDivider() {
    HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 4.dp))
}

internal class CascadeMenuItem(
    val key: Any,
    val content: @Composable MenuScope.() -> Unit,
    val depth: Int = 0,
    var location: Offset = Offset.Unspecified,
    var isVisible: MutableState<Boolean> = mutableStateOf(false),
)

class MenuState internal constructor() {
    internal val cascadeMenus = mutableMapOf<Any, CascadeMenuItem>()
    var selectedItem by mutableStateOf<Any?>(null)
    val visibleMenus get() = cascadeMenus.values.filter { it.isVisible.value }.map { it.key }

    internal fun setItemSelection(
        key: Any,
        selection: Boolean,
        depth: Int,
        cascadeMenuId: Any?,
    ) {
        if (!selection) {
            selectedItem = null
            return
        }
        selectedItem = key

        cascadeMenus.values
            .filter { it.depth >= depth && it.isVisible.value }
            .forEach { it.isVisible.value = false }

        if (cascadeMenuId != null) {
            cascadeMenus[cascadeMenuId]!!.isVisible.value = true
        }
    }
}

@LayoutScopeMarker
class MenuScope internal constructor(
    private val state: MenuState,
    private val depth: Int = 0,
) {
    internal val cascadeMenus = mutableListOf<CascadeMenuItem>()

    fun cascadeMenu(
        key: Any,
        content: @Composable MenuScope.() -> Unit,
    ) {
        val item = CascadeMenuItem(key, content, depth)
        cascadeMenus.add(item)
    }

    fun Modifier.menuItem(
        key: Any,
        enabled: Boolean = true,
        cascadeMenuId: Any? = null
    ) = composed {
        val selected = state.selectedItem == key ||
                if (cascadeMenuId == null) false else state.visibleMenus.contains(cascadeMenuId)

        selectionBackground(selected)
            .then(
                if (cascadeMenuId != null) {
                    Modifier.onPlaced { layoutCoordinates ->
                        val positionInParent = layoutCoordinates.positionInParent()
                        state.cascadeMenus[cascadeMenuId]!!.location =
                            positionInParent.copy(x = positionInParent.x + layoutCoordinates.size.width)
                    }
                } else {
                    Modifier
                }
            ).onHover(enabled) { state.setItemSelection(key, it, depth, cascadeMenuId) }
    }
}

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    content: @Composable MenuScope.(MenuState) -> Unit,
) {
    val state = remember(content) { MenuState() }
    val scope = remember(content) { MenuScope(state) }

    Layout(
        modifier = modifier,
        content = {
            MenuContent { scope.content(state) }

            if (scope.cascadeMenus.isEmpty()) return

            val cascadeMenus = mutableListOf<CascadeMenuItem>()
            cascadeMenus.addAll(scope.cascadeMenus)

            while (cascadeMenus.isNotEmpty()) {
                val current = cascadeMenus.removeFirst()
                state.cascadeMenus[current.key] = current

                val scope = MenuScope(state, current.depth + 1)
                MenuContent(Modifier.layoutId(current.key)) {
                    current.content(scope)
                }

                cascadeMenus.addAll(scope.cascadeMenus)
            }
        },
        measurePolicy = { measurables, constraints ->
            val rootMenu = measurables.first().measure(constraints)
            val visibleSubMenus = state.cascadeMenus.filter { it.value.isVisible.value }
            val cascadeMenus = if (visibleSubMenus.isEmpty()) emptyMap()
            else measurables.drop(1).associate { it.layoutId to it.measure(constraints) }
                .filter { visibleSubMenus.contains(it.key) }

            val cascadeMenusHeight = cascadeMenus.map {
                state.cascadeMenus[it.key]!!.location.y.toInt() + it.value.height
            }.sum()

            layout(
                width = rootMenu.width + cascadeMenus.values.sumOf { it.width },
                height = cascadeMenusHeight + rootMenu.height,
            ) {
                rootMenu.place(0, 0)
                var x = rootMenu.width - 4
                var y = 0

                if (cascadeMenus.isEmpty()) return@layout

                for ((id, cascadeMenu) in visibleSubMenus) {
                    val menu = cascadeMenus[id] ?: continue
                    y += cascadeMenu.location.y.toInt()
                    menu.place(x, y)
                    x += menu.width - 4
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
