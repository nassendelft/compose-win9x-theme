package nl.ncaj.theme.win9x

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

object SelectionIndication : Indication {

    private class SelectionIndication(
        private val isFocused: State<Boolean>,
        private val color: Color
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            if (isFocused.value) drawRect(color, size = size)
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isFocused = interactionSource.collectIsFocusedAsState()
        val color = Win9xTheme.colorScheme.selection
        return remember(interactionSource) {
            SelectionIndication(isFocused, color)
        }
    }
}

class DashFocusIndication(
    private val padding: Dp = Dp.Unspecified
) : Indication {

    private class DashFocusIndicationInstance(
        private val isFocused: State<Boolean>,
        private val padding: Dp,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            drawContent()
            if (isFocused.value) drawDashFocus(padding)
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isFocused = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) { DashFocusIndicationInstance(isFocused, padding) }
    }

    companion object {
        val DashFocusIndicationNoPadding = DashFocusIndication()
    }
}

class ColorPressIndication private constructor(
    private val pressColor: Color = Color.Unspecified,
    private val backgroundColor: Color = Color.Unspecified
) : Indication {

    private class ColorPressIndicationInstance(
        private val isPressed: State<Boolean>,
        private val pressedColor: Color,
        private val backgroundColor: Color,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            if (isPressed.value) drawRect(pressedColor)
            else drawRect(backgroundColor)
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isPressed = interactionSource.collectIsPressedAsState()
        return remember(interactionSource) {
            ColorPressIndicationInstance(isPressed, pressColor, backgroundColor)
        }
    }

    companion object {
        @Composable
        fun Modifier.colorIndication(
            interactionSource: InteractionSource,
            pressColor: Color = Win9xTheme.colorScheme.buttonShadow,
            backgroundColor: Color = Win9xTheme.colorScheme.windowFrame
        ) = this.indication(interactionSource, ColorPressIndication(pressColor, backgroundColor))
    }
}

internal fun DrawScope.drawDashFocus(padding: Dp = Dp.Unspecified) {
    val size = if (padding == Dp.Unspecified) size
    else Size(size.width - (padding.toPx() * 2), size.height - (padding.toPx() * 2))
    val offset = if (padding == Dp.Unspecified) Offset.Zero
    else Offset(padding.toPx(), padding.toPx())
    drawRect(
        color = Color.Black,
        size = size,
        topLeft = offset,
        style = Stroke(
            width = 1f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)
        )
    )
}
