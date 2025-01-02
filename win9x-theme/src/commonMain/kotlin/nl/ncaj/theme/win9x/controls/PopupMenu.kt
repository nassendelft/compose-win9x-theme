package nl.ncaj.theme.win9x.controls

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset


@Composable
expect fun PopupMenu(
    offset: IntOffset = IntOffset.Zero,
    onDismissRequested: () -> Unit,
    subMenu: @Composable MenuScope.(subMenuId: Any) -> Unit = {},
    content: @Composable MenuScope.() -> Unit,
)
