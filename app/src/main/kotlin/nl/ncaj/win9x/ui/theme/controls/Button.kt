package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.ButtonIndication
import nl.ncaj.win9x.ui.theme.Win98Border
import nl.ncaj.win9x.ui.theme.Win98Theme

@Composable
@Preview
fun ButtonPreview() {
    Column {
        Text("- Button -")
        Spacer(modifier = Modifier.height(2.dp))
        Button(onClick = {}) {
            Text("Default")
        }
        Spacer(modifier = Modifier.height(2.dp))
        Button(onClick = {}, enabled = false) {
            Text("Disabled", enabled = false)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Button(onClick = {}, Modifier.size(20.dp)) {
            Image(painter = painterResource(id = R.drawable.ic_cross), contentDescription = "")
        }
    }
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borders: Win98ButtonBorders = defaultButtonBorders(),
    background: Color = Win98Theme.colorScheme.buttonFace,
    enabled: Boolean = true,
    defaultPadding: PaddingValues = PaddingValues(4.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .defaultMinSize(75.dp, 23.dp)
            .background(background)
            .clickable(
                interactionSource = interactionSource,
                indication = ButtonIndication(borders, enabled),
                onClick = onClick,
            )
            .padding(defaultPadding)
            .then(if (enabled && isPressed) Modifier.offset(1.dp, 1.dp) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

class Win98ButtonBorders(
    val normal: Win98Border,
    val pressed: Win98Border
) {
    fun copy(
        normal: Win98Border = this.normal,
        pressed: Win98Border = this.pressed
    ) = Win98ButtonBorders(normal, pressed)
}

@Composable
internal fun defaultButtonBorders() = Win98ButtonBorders(
    normal = Win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonHighlight,
        innerStartTop = Win98Theme.colorScheme.buttonFace,
        outerEndBottom = Win98Theme.colorScheme.buttonShadow,
        innerEndBottom = Win98Theme.colorScheme.windowFrame,
        borderWidth = Win98Theme.borderWidthPx
    ),
    pressed = Win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        innerStartTop = Win98Theme.colorScheme.windowFrame,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        innerEndBottom = Win98Theme.colorScheme.buttonFace,
        borderWidth = Win98Theme.borderWidthPx
    )
)

@Composable
internal fun innerButtonBorders() = Win98ButtonBorders(
    normal = Win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonFace,
        innerStartTop = Win98Theme.colorScheme.buttonHighlight,
        outerEndBottom = Win98Theme.colorScheme.windowFrame,
        innerEndBottom = Win98Theme.colorScheme.buttonShadow,
        borderWidth = Win98Theme.borderWidthPx
    ),
    pressed = Win98Border(
        outerStartTop = Win98Theme.colorScheme.windowFrame,
        innerStartTop = Win98Theme.colorScheme.buttonShadow,
        outerEndBottom = Win98Theme.colorScheme.buttonFace,
        innerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        borderWidth = Win98Theme.borderWidthPx
    )
)