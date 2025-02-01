package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.FocusDashIndication.Companion.focusDashIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.buttonNormalBorder
import nl.ncaj.theme.win9x.buttonPressedBorder
import nl.ncaj.theme.win9x.checkeredBackground

@Composable
fun OptionSetButton(
    set: Boolean,
    onSetChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    defaultPadding: PaddingValues = PaddingValues(4.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .defaultMinSize(75.dp, 23.dp)
            .then(
                if (set) {
                    Modifier.checkeredBackground(
                        Win9xTheme.colorScheme.buttonFace,
                        Win9xTheme.colorScheme.buttonHighlight
                    )
                } else Modifier.background(Win9xTheme.colorScheme.buttonFace)
            )
            .then(
                if (set || isPressed) Modifier.buttonPressedBorder()
                else Modifier.buttonNormalBorder()
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = { onSetChanged(!set) },
            )
            .focusDashIndication(interactionSource, padding = 3.dp)
            .padding(defaultPadding)
            .then(if ((enabled && isPressed) || set) Modifier.offset(1.dp, 1.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}