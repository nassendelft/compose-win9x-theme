package nl.ncaj.win9x.ui.theme.controls


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import nl.ncaj.win9x.ui.theme.rememberVectorResourcePainter

@Composable
internal fun OptionButtonPreview() {
    var checked by remember { mutableStateOf(true) }
    Column {
        Text("- Option Buttons -")
        Spacer(modifier = Modifier.height(2.dp))
        OptionButton(checked = checked, onCheckChange = { checked = it }) {
            Text("Default")
        }
        OptionButton(checked = false, onCheckChange = {}, enabled = false) {
            Text("Disabled", enabled = false)
        }
        OptionButton(checked = true, onCheckChange = {}, enabled = false) {
            Text("Disabled checked", enabled = false)
        }
    }
}

@Composable
fun OptionButton(
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable BoxScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val background =
        if (enabled) rememberVectorResourcePainter("vector_images/bg_option_button.xml")
        else rememberVectorResourcePainter("vector_images/bg_option_button_disabled.xml")
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
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = background,
                contentDescription = "",
            )
            if (checked) {
                Image(
                    painter = rememberVectorResourcePainter("vector_images/ic_option_button.xml"),
                    contentDescription = "checked",
                    colorFilter = ColorFilter.tint(if (enabled) Color.Black else Color(0xFF808080))
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
