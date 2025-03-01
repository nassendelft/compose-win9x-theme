package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
    menu: @Composable MenuScope.(menuId: Any) -> Unit = {},
) {
    var displayMenu by remember { mutableStateOf(false) }
    var buttonSet by remember { mutableStateOf(false) }

    Column(modifier) {
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
                    menu = menu
                )
            }
        }
    }
}