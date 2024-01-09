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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.sunkenBorder

@Composable
@Preview
fun ListBoxPreview() {
    var selection by remember { mutableIntStateOf(0) }

    Column {
        Text("- List box -")
        Spacer(modifier = Modifier.height(2.dp))

        ListBox(
            modifier = Modifier.widthIn(min = 100.dp)
        ) {
            Item(
                label = "Value 1",
                onSelected = { selection = 0 },
                selected = selection == 0
            )
            Item(
                label = "Value 2",
                enabled = false,
                onSelected = { selection = 1 },
                selected = selection == 1
            )
            Item(
                label = "Value 3",
                onSelected = { selection = 3 },
                selected = selection == 3
            )
        }
    }
}

class ListBoxScope(private val columnScope: ColumnScope) {
    @Composable
    fun Item(
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

        columnScope.apply {
            Row(
                modifier = Modifier
                    .clickable(
                        enabled = enabled,
                        onClick = onSelected,
                    )
                    .fillMaxWidth()
                    .then(
                        if (isSelected) Modifier.background(Win98Theme.colorScheme.selection)
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
    }
}

@Composable
fun ListBox(
    modifier: Modifier = Modifier,
    content: @Composable ListBoxScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .width(IntrinsicSize.Max)
            .sunkenBorder()
            .padding(Win98Theme.borderWidthDp + 2.dp)
    ) {
        ListBoxScope(this).content()
    }
}