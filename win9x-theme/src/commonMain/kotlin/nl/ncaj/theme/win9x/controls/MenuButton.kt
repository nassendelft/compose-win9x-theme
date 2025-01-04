package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


@Composable
fun MenuButton(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit = {},
    menu: @Composable MenuScope.() -> Unit,
) {
    var displayMenu by remember { mutableStateOf(false) }
    var buttonSet by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OptionSetButton(
            set = buttonSet,
            onSetChanged = {
                displayMenu = true
                buttonSet = it
            },
            content = { label() }
        )
        if (displayMenu) {
            Box {
                PopupMenu(
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
}