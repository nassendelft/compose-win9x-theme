package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.SelectionIndication

@Preview
@Composable
fun MenuBarPreview() {
    MenuBar {
        entry("Item1") {
            label("Sub menu item 1") {}
        }
        entry("Item2") {
            label("Sub menu item 1") {}
            cascade("Sub menu item 2") {
                label("Cascade menu item 1") {}
            }
        }
    }
}

class MenuBarItem(
    internal val title: String,
    internal val enabled: Boolean,
    internal val content: MenuScope.() -> Unit
)

class MenuBarScope {
    internal val items = mutableListOf<MenuBarItem>()

    fun entry(
        title: String,
        enabled: Boolean = true,
        content: MenuScope.() -> Unit
    ) = items.add(MenuBarItem(title, enabled, content))
}

@Composable
fun MenuBar(
    modifier: Modifier = Modifier,
    content: MenuBarScope.() -> Unit,
) {
    val scope = MenuBarScope().apply(content)
    var barHeight by remember { mutableIntStateOf(0) }
    Row(
        modifier = modifier.onGloballyPositioned { barHeight = it.size.height }
    ) {
        scope.items.forEach { item ->
            var currentMenu by remember { mutableStateOf<(MenuScope.() -> Unit)?>(null) }

            val mutableInteractionSource = remember { MutableInteractionSource() }
            val isFocused by mutableInteractionSource.collectIsFocusedAsState()
            val isPressed by mutableInteractionSource.collectIsPressedAsState()
            Box {// box is needed to get PopupMenu aligned properly
                Text(
                    text = item.title,
                    modifier = Modifier
                        .clickable(
                            interactionSource = mutableInteractionSource,
                            indication = SelectionIndication,
                            onClick = { currentMenu = item.content }
                        )
                        .padding(4.dp),
                    color = if (isFocused || isPressed) ColorProducer { Color.White } else null
                )

                currentMenu?.let {
                    PopupMenu(
                        offset = IntOffset(0, barHeight),
                        onDismissRequested = { currentMenu = null },
                        content = it
                    )
                }
            }
            Spacer(Modifier.width(4.dp))
        }
    }
}