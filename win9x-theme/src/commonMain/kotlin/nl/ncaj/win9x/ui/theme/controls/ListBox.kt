package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.sunkenBorder

class ListBoxScope internal constructor() {
    internal val items = mutableListOf<@Composable () -> Unit>()

    fun item(content: @Composable () -> Unit) = items.add(content)
}

@Composable
fun ListBox(
    modifier: Modifier = Modifier,
    content: ListBoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .width(IntrinsicSize.Max)
            .sunkenBorder()
            .padding(Win9xTheme.borderWidthDp + 2.dp),
        content = { ListBoxScope().apply(content).items.forEach { it() } }
    )
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
