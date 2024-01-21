package nl.ncaj.win9x.ui.theme.controls


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.DashFocusIndication.Companion.DashFocusIndicationNoPadding
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.rememberVectorResourcePainter
import nl.ncaj.win9x.ui.theme.sunkenBorder

@Composable
internal fun CheckboxPreview() {
    var checked by remember { mutableStateOf(true) }
    Column {
        Text("- Check boxes -")
        Spacer(modifier = Modifier.height(2.dp))
        Checkbox(checked = checked, onCheckChange = { checked = it }) {
            Text("Default")
        }
        Checkbox(checked = false, onCheckChange = {}, enabled = false) {
            Text("Disabled", enabled = false)
        }
        Checkbox(checked = true, onCheckChange = {}, enabled = false) {
            Text("Disabled checked", enabled = false)
        }
    }
}

@Composable
fun Checkbox(
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable BoxScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onCheckChange(!checked) }
            )
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(13.dp, 13.dp)
                .background(if (enabled) Win9xTheme.colorScheme.buttonHighlight else Win9xTheme.colorScheme.buttonFace)
                .sunkenBorder(),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Image(
                    painter = rememberVectorResourcePainter("vector_images/ic_checkmark.xml"),
                    contentDescription = "checked",
                    colorFilter = ColorFilter.tint(if (enabled) Color.Black else Color(0xFF808080)),
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Box(
            modifier = Modifier.indication(interactionSource, DashFocusIndicationNoPadding),
            content = label
        )
    }
}
