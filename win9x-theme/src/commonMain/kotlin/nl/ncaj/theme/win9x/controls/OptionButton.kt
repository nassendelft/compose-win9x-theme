package nl.ncaj.theme.win9x.controls


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.DashFocusIndication.Companion.DashFocusIndicationNoPadding
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.icons.Icons
import nl.ncaj.theme.win9x.icons.OptionButtonDot
import nl.ncaj.theme.win9x.icons.optionButton


@Composable
fun OptionButton(
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val backgroundColor = when {
        isPressed && enabled -> Win9xTheme.colorScheme.buttonShadow
        enabled -> Win9xTheme.colorScheme.buttonHighlight
        else -> Color(0xC0, 0xC0, 0xC0)
    }
    Row(
        modifier = modifier
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
                painter = rememberVectorPainter(Icons.optionButton(backgroundColor)),
                contentDescription = "",
            )
            if (checked) {
                Image(
                    painter = rememberVectorPainter(Icons.OptionButtonDot),
                    contentDescription = if (checked) "checked" else "unchecked",
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
