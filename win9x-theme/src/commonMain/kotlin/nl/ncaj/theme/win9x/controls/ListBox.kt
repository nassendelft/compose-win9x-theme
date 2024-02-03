package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder

class ListBoxScope internal constructor() {
    internal val items = mutableListOf<@Composable () -> Unit>()

    fun item(content: @Composable () -> Unit) = items.add(content)
}

@Composable
fun ListBox(
    modifier: Modifier = Modifier,
    content: ListBoxScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = modifier
            .background(Color.White)
            .sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .verticalScroll(scrollState),
            content = { ListBoxScope().apply(content).items.forEach { it() } }
        )
        VerticalScrollBar(scrollState = scrollState)
    }
}

@Composable
fun DropDownListBoxItem(
    label: String,
    enabled: Boolean = true,
    selected: Boolean = false,
    onSelected: () -> Unit = {},
    leadingIcon: @Composable (RowScope.() -> Unit)? = null,
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    val isPressed = mutableInteractionSource.collectIsPressedAsState()
    val isHover = mutableInteractionSource.collectIsHoveredAsState()
    val isFocused = mutableInteractionSource.collectIsFocusedAsState()
    val isSelected = selected || isPressed.value || isHover.value || isFocused.value

    Row(
        modifier = Modifier
            .clickable(
                enabled = enabled,
                onClick = onSelected,
            )
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.background(Win9xTheme.colorScheme.selection)
                else Modifier
            )
            .padding(4.dp),
    ) {
        leadingIcon?.invoke(this)?.also { Spacer(Modifier.width(4.dp)) }

        Text(
            label,
            enabled = enabled,
            color = if (isSelected) ColorProducer { Color.White } else null
        )
    }
}
