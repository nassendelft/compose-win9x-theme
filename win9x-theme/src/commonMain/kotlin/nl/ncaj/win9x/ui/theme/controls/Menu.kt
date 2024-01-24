package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.rememberVectorResourcePainter
import nl.ncaj.win9x.ui.theme.windowBorder
import kotlin.math.roundToInt


class MenuItem(
    internal val subMenu: MenuScope? = null,
    internal val content: @Composable () -> Unit,
)

private val dividerMenuItem = MenuItem { Divider() }

class MenuScope {
    internal val items = mutableListOf<MenuItem>()
    internal var showCascadeMenu by mutableStateOf(false)
    internal var cascadeItemOffset by mutableIntStateOf(0)

    fun label(
        label: String,
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        onClick: () -> Unit,
    ) = items.add(
        MenuItem {
            Label(
                label = label,
                enabled = enabled,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                onClick = onClick
            )
        }
    )

    fun checkBox(
        label: String,
        enabled: Boolean = true,
        checked: Boolean = false,
        onCheckChanged: (Boolean) -> Unit,
    ) {
        val menuItem = MenuItem {
            Label(
                label = label,
                modifier = Modifier,
                enabled = enabled,
                leadingIcon = {
                    if (checked) {
                        Image(
                            painter = rememberVectorResourcePainter("vector_images/ic_checkmark.xml"),
                            contentDescription = "checked",
                            colorFilter = ColorFilter.tint(
                                if (enabled) Color.Black else Color(0xFF808080)
                            ),
                        )
                    }
                },
                onClick = { onCheckChanged(!checked) }
            )
        }
        items.add(menuItem)
    }

    fun optionButton(
        label: String,
        enabled: Boolean = true,
        checked: Boolean = false,
        onCheckChanged: (Boolean) -> Unit,
    ) {
        val menuItem = MenuItem {
            Label(
                label = label,
                modifier = Modifier,
                enabled = enabled,
                leadingIcon = {
                    if (checked) {
                        Image(
                            painter = rememberVectorResourcePainter("vector_images/bg_option_button.xml"),
                            contentDescription = "checked",
                            colorFilter = ColorFilter.tint(
                                if (enabled) Color.Black else Color(0xFF808080)
                            )
                        )
                    }
                },
                onClick = { onCheckChanged(!checked) }
            )
        }
        items.add(menuItem)
    }

    fun cascade(
        label: String,
        enabled: Boolean = true,
        content: MenuScope.() -> Unit,
    ) {
        val scope = MenuScope().apply(content)

        val menuItem = MenuItem(scope) {
            var yPosition by remember { mutableFloatStateOf(0f) }
            val image =
                if (enabled) rememberVectorResourcePainter("vector_images/ic_arrow_down.xml")
                else rememberVectorResourcePainter("vector_images/ic_arrow_down_disabled.xml")

            val mutableInteractionSource = remember { MutableInteractionSource() }
            val isFocused by mutableInteractionSource.collectIsFocusedAsState()
            val isHovered by mutableInteractionSource.collectIsHoveredAsState()

            Label(
                modifier = Modifier.onGloballyPositioned { yPosition = it.positionInParent().y },
                mutableInteractionSource = mutableInteractionSource,
                label = label,
                enabled = enabled,
                trailingIcon = {
                    Image(
                        painter = image,
                        contentDescription = "checked",
                        modifier = Modifier.rotate(-90f),
                        colorFilter = if (isFocused || isHovered) ColorFilter.tint(Color.White) else null
                    )
                },
                onClick = {
                    showCascadeMenu = !showCascadeMenu
                    cascadeItemOffset = yPosition.roundToInt()
                }
            )
        }
        items.add(menuItem)
    }

    fun divider() = items.add(dividerMenuItem)

    @Composable
    private fun Label(
        label: String,
        modifier: Modifier = Modifier,
        mutableInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        onClick: () -> Unit
    ) {
        val isFocused by mutableInteractionSource.collectIsFocusedAsState()
        val isHover by mutableInteractionSource.collectIsHoveredAsState()
        val focusBackgroundColor = Win9xTheme.colorScheme.selection

        Row(
            modifier = modifier
                .padding(horizontal = 1.dp, vertical = 3.dp)
                .clickable(
                    interactionSource = mutableInteractionSource,
                    indication = null,
                    onClick = onClick
                )
                .drawBehind { if (isFocused || isHover) drawRect(focusBackgroundColor, size = size) },
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
                color = if (isFocused || isHover) ColorProducer { Color.White } else null
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.size(15.dp),
                contentAlignment = Alignment.Center,
                content = { trailingIcon?.invoke() },
            )
        }
    }
}

@Composable
fun Menu(
    modifier: Modifier = Modifier,
    content: MenuScope.() -> Unit
) {
    val rootScope = remember { MenuScope().apply(content) }

    Layout(
        modifier = modifier,
        content = {
            var currentMenu: MenuScope? = rootScope
            while (currentMenu != null) {
                MenuContent(scope = currentMenu)
                currentMenu = if (currentMenu.showCascadeMenu)
                    currentMenu.items.firstNotNullOfOrNull { it.subMenu }
                else null
            }
        }
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }
        val width = placeables.fold(0) { acc, placeable -> acc + placeable.width }
        val height = calculateHeight(rootScope, placeables)

        layout(width, height) {
            var position = IntOffset.Zero
            var currentMenu: MenuScope? = rootScope
            var index = 0
            while (currentMenu != null) {
                placeables[index].place(position)
                position += IntOffset(
                    placeables[index].width - 4,
                    currentMenu?.cascadeItemOffset ?: 0
                )
                currentMenu = currentMenu?.let { scope ->
                    if (scope.showCascadeMenu) scope.items.firstNotNullOfOrNull { it.subMenu } else null
                }
                index++
            }
        }
    }
}

private fun calculateHeight(
    rootScope: MenuScope,
    placeables: List<Placeable>
): Int {
    var height = 0
    var previousOffset = 0
    var currentMenu: MenuScope? = rootScope
    var index = 0
    while (currentMenu != null) {
        height += (placeables[index].height + previousOffset)
        previousOffset = currentMenu.cascadeItemOffset
        currentMenu = if (currentMenu.showCascadeMenu)
            currentMenu.items.firstNotNullOfOrNull { it.subMenu }
        else null

        index++
    }
    return height
}

@Composable
private fun MenuContent(
    scope: MenuScope,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .background(Win9xTheme.colorScheme.buttonFace)
            .windowBorder()
            .padding(Win9xTheme.borderWidthDp),
        content = { scope.items.forEach { item -> item.content() } }
    )
}
