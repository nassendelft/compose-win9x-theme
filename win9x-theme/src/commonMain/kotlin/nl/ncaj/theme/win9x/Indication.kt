package nl.ncaj.theme.win9x

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


class SelectionIndication private constructor(private val color: Color) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        SelectionIndicationInstance(interactionSource, color)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SelectionIndication

        return color == other.color
    }

    override fun hashCode() = color.hashCode()

    private class SelectionIndicationInstance(
        private val interactionSource: InteractionSource,
        private val color: Color
    ) : Modifier.Node(), DrawModifierNode {
        private var isFocused = false

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.collectIsFocused().collect { focused ->
                    isFocused = focused
                    invalidateDraw()
                }
            }
        }

        override fun ContentDrawScope.draw() {
            if (isFocused) drawRect(color, size = size)
            drawContent()
        }
    }

    companion object {
        @Composable
        fun Modifier.selectionIndication(
            interactionSource: InteractionSource,
            color: Color = Win9xTheme.colorScheme.selection,
        ) = this.indication(interactionSource, SelectionIndication(color))

        @Composable
        fun create(
            color: Color = Win9xTheme.colorScheme.selection
        ) = SelectionIndication(color)
    }
}

class DashFocusIndication(
    private val padding: Dp = Dp.Unspecified
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        DashIndicationInstance(interactionSource, padding)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as DashFocusIndication

        return padding == other.padding
    }

    override fun hashCode(): Int = padding.hashCode()

    companion object {
        val DashFocusIndicationNoPadding = DashFocusIndication()
    }

    private class DashIndicationInstance(
        private val interactionSource: InteractionSource,
        private val padding: Dp
    ) : Modifier.Node(), DrawModifierNode {
        private var isFocused = false

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.collectIsFocused().collect { focused ->
                    isFocused = focused
                    invalidateDraw()
                }
            }
        }

        override fun ContentDrawScope.draw() {
            drawContent()
            if (isFocused) drawDashFocus(padding)
        }
    }
}

class ColorPressIndication private constructor(
    private val pressColor: Color = Color.Unspecified,
    private val backgroundColor: Color = Color.Unspecified
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ColorPressIndicationInstance(interactionSource, pressColor, backgroundColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as ColorPressIndication

        if (pressColor != other.pressColor) return false
        if (backgroundColor != other.backgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pressColor.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        return result
    }

    private class ColorPressIndicationInstance(
        private val interactionSource: InteractionSource,
        private val pressedColor: Color,
        private val backgroundColor: Color,
    ) : Modifier.Node(), DrawModifierNode {
        private var isPressed = false

        override fun onAttach() {
            coroutineScope.launch {
                interactionSource.collectIsPressed().collect { pressed ->
                    isPressed = pressed
                    invalidateDraw()
                }
            }
        }

        override fun ContentDrawScope.draw() {
            if (isPressed) drawRect(pressedColor)
            else drawRect(backgroundColor)
            drawContent()
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

private fun InteractionSource.collectIsFocused() = flow {
    var focusCount = 0
    var isFocused = false
    interactions.collect { interaction ->
        when (interaction) {
            is FocusInteraction.Focus -> focusCount++
            is FocusInteraction.Unfocus -> focusCount--
        }
        val focused = focusCount > 0
        if (isFocused != focused) {
            isFocused = focused
            emit(isFocused)
        }
    }
}

private fun InteractionSource.collectIsPressed() = flow {
    var pressCount = 0
    var isPressed = false
    interactions.collect { interaction ->
        when (interaction) {
            is PressInteraction.Press -> pressCount++
            is PressInteraction.Release -> pressCount--
            is PressInteraction.Cancel -> pressCount--
        }
        val pressed = pressCount > 0
        if (isPressed != pressed) {
            isPressed = pressed
            emit(isPressed)
        }
    }
}