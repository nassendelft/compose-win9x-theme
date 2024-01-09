package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.drawDashFocus
import nl.ncaj.win9x.ui.theme.drawWin98Border

@Composable
@Preview
fun ButtonPreview() {
    Column {
        Text("- Buttons -")
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
    borders: @Composable () -> Win98ButtonBorders = { defaultButtonBorders() },
    enabled: Boolean = true,
    defaultPadding: PaddingValues = PaddingValues(4.dp),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .defaultMinSize(75.dp, 23.dp)
            .background(Win98Theme.colorScheme.buttonFace)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(
                interactionSource = interactionSource,
                indication = ButtonIndication(borders(), enabled),
                onClick = onClick,
            )
            .padding(defaultPadding),
        contentAlignment = Alignment.Center,
    ) {
        if (enabled && isPressed) Box(Modifier.offset(1.dp, 1.dp)) { content() } else content()
    }
}

internal class ButtonIndication(
    private val borders: Win98ButtonBorders,
    private val enabled: Boolean,
) : Indication {

    private class ButtonIndicationInstance(
        private val isFocused: State<Boolean>,
        private val isPressed: State<Boolean>,
        private val enabled: Boolean,
        private val borders: Win98ButtonBorders,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            if (isPressed.value && enabled) {
                drawWin98Border(
                    borders.pressed.outerStartTop,
                    borders.pressed.innerStartTop,
                    borders.pressed.outerEndBottom,
                    borders.pressed.innerEndBottom,
                    borders.pressed.borderWidth
                )
            } else {
                drawWin98Border(
                    borders.normal.outerStartTop,
                    borders.normal.innerStartTop,
                    borders.normal.outerEndBottom,
                    borders.normal.innerEndBottom,
                    borders.normal.borderWidth
                )
            }
            drawContent()
            if (isFocused.value) drawDashFocus(3.dp)
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isFocused = interactionSource.collectIsFocusedAsState()
        val isPressed = interactionSource.collectIsPressedAsState()
        return remember(interactionSource) {
            ButtonIndicationInstance(isFocused, isPressed, enabled, borders)
        }
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

class Win98Border(
    val outerStartTop: Color,
    val outerEndBottom: Color,
    val innerStartTop: Color,
    val innerEndBottom: Color,
    val borderWidth: Float
) {
    fun copy(
        outerStartTop: Color = this.outerStartTop,
        outerEndBottom: Color = this.outerEndBottom,
        innerStartTop: Color = this.innerStartTop,
        innerEndBottom: Color = this.innerEndBottom,
        borderWidth: Float = this.borderWidth
    ) = Win98Border(outerStartTop, outerEndBottom, innerStartTop, innerEndBottom, borderWidth)
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