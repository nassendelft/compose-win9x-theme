package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun MenuButtonPreview() {
    Column {
        Text("- Menu Button -")
        Spacer(modifier = Modifier.height(2.dp))
        MenuButton(
            menu = {
                label("Command") {}
                divider()
                cascade("Check Box") {
                    label("Command") {}
                }
            }
        ) {
            Text("Menu")
        }
    }
}

@Composable
fun MenuButton(
    menu: MenuScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var displayMenu by remember { mutableStateOf(false) }
    var buttonSet by remember { mutableStateOf(false) }
    var buttonHeight by remember { mutableIntStateOf(0) }

    Box(modifier) {
        OptionSetButton(
            set = buttonSet,
            modifier = Modifier.onGloballyPositioned {
                buttonHeight = it.size.height
            },
            onSetChanged = {
                displayMenu = true
                buttonSet = it
            },
            content = { content() }
        )
        if (displayMenu) {
            PopupMenu(
                offset = IntOffset(0, buttonHeight),
                onDismissRequested = {
                    displayMenu = false
                    buttonSet = false
                },
                content = menu
            )
        }
    }
}