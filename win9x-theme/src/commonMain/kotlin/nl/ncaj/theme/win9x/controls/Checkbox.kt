package nl.ncaj.theme.win9x.controls


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.ColorPressIndication.Companion.colorIndication
import nl.ncaj.theme.win9x.DashFocusIndication.Companion.DashFocusIndicationNoPadding
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder
import nl.ncaj.theme.win9x.vector.Checkmark
import nl.ncaj.theme.win9x.vector.Icons

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
                .colorIndication(
                    interactionSource = interactionSource,
                    pressColor = if(enabled) Win9xTheme.colorScheme.buttonShadow else Color.Unspecified,
                    backgroundColor = if (enabled) Win9xTheme.colorScheme.buttonHighlight else Win9xTheme.colorScheme.buttonFace
                )
                .sunkenBorder(),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.Checkmark),
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
