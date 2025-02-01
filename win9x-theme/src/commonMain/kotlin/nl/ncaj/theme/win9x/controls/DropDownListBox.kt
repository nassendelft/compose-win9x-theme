package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import nl.ncaj.theme.win9x.FocusDashIndication
import nl.ncaj.theme.win9x.FocusDashIndication.Companion.focusDashIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.indication
import nl.ncaj.theme.win9x.sunkenBorder
import nl.ncaj.theme.win9x.vector.ArrowDown
import nl.ncaj.theme.win9x.vector.Icons
import nl.ncaj.theme.win9x.win9xBorder


@Composable
fun DropDownListBox(
    text: String,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    enabled: Boolean = true,
    onExpandChange: (expanded: Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) = with(LocalDensity.current) {
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borders = innerButtonBorders()

    Box {
        Row(
            modifier = modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { onExpandChange(!expanded) }
                )
                .onGloballyPositioned { containerSize = it.size }
                .background(if (enabled) Win9xTheme.colorScheme.buttonHighlight else Win9xTheme.colorScheme.buttonFace)
                .sunkenBorder()
                .padding(Win9xTheme.borderWidthDp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .padding(horizontal = 6.dp, vertical = 4.dp)
                    .weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(14.dp, 23.dp)
                    .background(Win9xTheme.colorScheme.buttonFace)
                    .focusDashIndication(interactionSource, 3.dp)
                    .then(
                        if (enabled && isPressed) Modifier.win9xBorder(borders.pressed)
                        else Modifier.win9xBorder(borders.normal)
                    )
                    .padding(PaddingValues(4.dp))
                    .then(if (enabled && isPressed) Modifier.offset(1.dp, 1.dp) else Modifier),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = rememberVectorPainter(Icons.ArrowDown),
                    contentDescription = "",
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
        if (expanded) {
            Popup(
                offset = IntOffset(0, containerSize.height),
                onDismissRequest = { onExpandChange(false) },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .width(containerSize.width.toDp())
                        .background(Win9xTheme.colorScheme.buttonHighlight)
                        .border(BorderStroke(1.dp, Win9xTheme.colorScheme.windowFrame)),
                    content = content
                )
            }
        }
    }
}

@Composable
fun DropDownListBoxItem(
    text: String,
    onSelection: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Text(
        enabled = enabled,
        text = text,
        style = textStyle,
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onSelection,
                interactionSource = interactionSource,
                indication = null
            )
            .onFocusChanged { if(it.isFocused) onSelection() }
            .fillMaxWidth()
            .then(
                if(isFocused) Modifier.background(Win9xTheme.colorScheme.selection)
                else Modifier
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
    )
}
