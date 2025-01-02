package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.unit.IntOffset


@Composable
fun MenuButton(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit = {},
    menu: @Composable MenuScope.() -> Unit,
) {
    var displayMenu by remember { mutableStateOf(false) }
    var buttonSet by remember { mutableStateOf(false) }
    var buttonHeight by remember { mutableIntStateOf(0) }

    Box(modifier) {
        OptionSetButton(
            set = buttonSet,
            modifier = Modifier.onPlaced { buttonHeight = it.size.height },
            onSetChanged = {
                displayMenu = true
                buttonSet = it
            },
            content = { label() }
        )
        if (displayMenu) {
            PopupMenu(
                offset = IntOffset(0, buttonHeight),
                onDismissRequested = {
                    displayMenu = false
                    buttonSet = false
                },
                subMenu = subMenu,
                content = menu
            )
        }
    }
}