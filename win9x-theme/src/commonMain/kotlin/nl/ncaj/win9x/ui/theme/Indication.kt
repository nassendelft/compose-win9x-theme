package nl.ncaj.win9x.ui.theme

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.controls.Win9xButtonBorders

object SelectionIndication: Indication {

    private class SelectionIndication(
        private val isHovered: State<Boolean>,
        private val isFocused: State<Boolean>,
        private val color: Color
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            if(isHovered.value || isFocused.value) {
                drawRect(color, size = size)
            }
            drawContent()
        }
    }

    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource): IndicationInstance {
        val isHovered = interactionSource.collectIsHoveredAsState()
        val isFocused = interactionSource.collectIsFocusedAsState()
        val color = Win9xTheme.colorScheme.selection
        return remember(interactionSource) {
            SelectionIndication(isHovered, isFocused, color)
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

internal class ButtonIndication(
    private val borders: Win9xButtonBorders,
    private val enabled: Boolean,
) : Indication {

    private class ButtonIndicationInstance(
        private val isFocused: State<Boolean>,
        private val isPressed: State<Boolean>,
        private val enabled: Boolean,
        private val borders: Win9xButtonBorders,
    ) : IndicationInstance {
        override fun ContentDrawScope.drawIndication() {
            if (isPressed.value && enabled) {
                drawWin9xBorder(
                    borders.pressed.outerStartTop,
                    borders.pressed.innerStartTop,
                    borders.pressed.outerEndBottom,
                    borders.pressed.innerEndBottom,
                    borders.pressed.borderWidth
                )
            } else {
                drawWin9xBorder(
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
