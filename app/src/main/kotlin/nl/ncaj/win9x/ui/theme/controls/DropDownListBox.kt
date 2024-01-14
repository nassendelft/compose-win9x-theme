package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.SelectionIndication
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.sunkenBorder

@Composable
fun DropDownListBoxPreview() {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("- Spin box -")
        Spacer(modifier = Modifier.height(2.dp))

        DropDownListBox("value", expanded = expanded, onExpandChange = { expanded = it }) {
            DropDownListBoxItem(text = "Value", onClick = { })
            DropDownListBoxItem(text = "Value", onClick = { }, enabled = false)
        }
    }
}

@Composable
fun DropDownListBox(
    text: String,
    modifier: Modifier = Modifier,
    expanded: Boolean = false,
    enabled: Boolean = true,
    onExpandChange: (expanded: Boolean) -> Unit,
    content: @Composable DropDownMenuScope.() -> Unit
) {
    var containerSizePx by remember { mutableStateOf(IntSize.Zero) }
    val containerWidth = with(LocalDensity.current) { containerSizePx.width.toDp() }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onExpandChange(!expanded) }
            )
    ) {
        Row(
            modifier = Modifier
                .onSizeChanged { containerSizePx = it }
                .background(if (enabled) Win9xTheme.colorScheme.buttonHighlight else Win9xTheme.colorScheme.buttonFace)
                .sunkenBorder(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
            )
            Box(
                Modifier.padding(
                    top = Win9xTheme.borderWidthDp,
                    bottom = Win9xTheme.borderWidthDp,
                    end = Win9xTheme.borderWidthDp
                )
            ) {
                Button(
                    onClick = { onExpandChange(!expanded) },
                    interactionSource = interactionSource,
                    modifier = Modifier.width(14.dp),
                    borders = innerButtonBorders()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
        if (expanded) {
            Popup(
                offset = IntOffset(0, containerSizePx.height),
                onDismissRequest = { onExpandChange(false) },
            ) {
                Column(
                    modifier = Modifier
                        .width(containerWidth)
                        .background(Win9xTheme.colorScheme.buttonHighlight)
                        .border(BorderStroke(1.dp, Win9xTheme.colorScheme.windowFrame))
                ) {
                    DropDownMenuScope().content()
                }
            }
        }
    }
}

class DropDownMenuScope {
    @Composable
    fun DropDownListBoxItem(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    ) {
        Box(
            modifier = modifier
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = SelectionIndication
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Text(
                enabled = enabled,
                text = text
            )
        }
    }
}
