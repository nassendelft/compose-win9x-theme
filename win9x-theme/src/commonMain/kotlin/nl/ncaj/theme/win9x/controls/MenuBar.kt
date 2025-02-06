package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.selectionBackground


class MenuBarScope internal constructor(
    private val positions: MutableMap<Any, Float>,
    private val onToggleMenu: (Any) -> Unit,
    rowScope: RowScope
) : RowScope by rowScope {
    fun Modifier.menuBarItem(
        key: Any,
        selected: Boolean,
        interactionSource: MutableInteractionSource? = null,
        enabled: Boolean = true,
        onClick: () -> Unit = {},
    ) = this then Modifier
        .focusProperties { canFocus = false }
        .onGloballyPositioned { positions[key] = it.positionInParent().x }
        .clickable(enabled = enabled, interactionSource = interactionSource, indication = null) {
            onToggleMenu(key)
            onClick()
        }
        .selectionBackground(selected)
        .padding(horizontal = 4.dp, vertical = 2.dp)
}

@Composable
fun MenuBar(
    selectedMenu: Any?,
    onMenuSelected: (Any?) -> Unit,
    modifier: Modifier = Modifier,
    menu: @Composable MenuScope.(key: Any) -> Unit,
    content: @Composable MenuBarScope.() -> Unit,
) {
    val positions = remember { mutableMapOf<Any, Float>() }
    val onToggleMenu: (Any) -> Unit = { onMenuSelected(if (selectedMenu == null) it else null) }

    Column(modifier) {
        Row { MenuBarScope(positions, onToggleMenu, this).apply { content() } }
        selectedMenu?.let { menuKey ->
            Box { // box is needed to get PopupMenu aligned properly
                PopupMenu(
                    offset = IntOffset(positions[menuKey]!!.toInt(), 0),
                    onDismissRequested = { onMenuSelected(null) },
                    subMenu = { menu(it) },
                    content = { menu(menuKey) }
                )
            }
        }
    }
}
