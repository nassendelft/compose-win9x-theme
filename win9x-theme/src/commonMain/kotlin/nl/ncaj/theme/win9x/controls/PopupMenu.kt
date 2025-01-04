package nl.ncaj.theme.win9x.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset


@Composable
expect fun PopupMenu(
    onDismissRequested: () -> Unit,
    modifier: Modifier = Modifier,
    offset: IntOffset = IntOffset.Zero,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit = {},
    content: @Composable MenuScope.() -> Unit,
)
