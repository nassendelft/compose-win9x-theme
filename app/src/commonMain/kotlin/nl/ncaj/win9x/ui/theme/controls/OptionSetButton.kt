package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.DashFocusIndication
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.buttonNormalBorder
import nl.ncaj.win9x.ui.theme.buttonPressedBorder
import nl.ncaj.win9x.ui.theme.checkeredBackground

@Composable
@Preview
fun OptionSetButtonPreview() {
    var set by remember { mutableStateOf(false) }
    Column {
        Text("- Option Set Button -")
        Spacer(modifier = Modifier.height(2.dp))
        OptionSetButton(set, { set = it }) {
            Text("Default")
        }
        Spacer(modifier = Modifier.height(2.dp))
        OptionSetButton(set = true, {}) {
            Text("Set")
        }
        Spacer(modifier = Modifier.height(2.dp))
        OptionSetButton(set = true, {}, enabled = false) {
            Text("Set Disabled", enabled = false)
        }
    }
}

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
                onClick = { onSetChanged(!set) },
            )
            .indication(interactionSource, DashFocusIndication(3.dp))
            .padding(defaultPadding)
            .then(if ((enabled && isPressed) || set) Modifier.offset(1.dp, 1.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}