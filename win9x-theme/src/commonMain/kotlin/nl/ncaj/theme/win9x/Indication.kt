package nl.ncaj.theme.win9x

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.*
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


//// we use our own `indication` function, as the default one filters out focus states, which we don't want.
//internal fun Modifier.indication(
//    interactionSource: InteractionSource,
//    indication: IndicationNodeFactory
//) = this.then(IndicationModifierElement(interactionSource, indication))
//
///**
// * ModifierNodeElement to create [IndicationNodeFactory] instances. More complicated modifiers such
// * as [clickable] should manually delegate to the node returned by [IndicationNodeFactory]
// * internally.
// */
//private class IndicationModifierElement(
//    private val interactionSource: InteractionSource,
//    private val indication: IndicationNodeFactory
//) : ModifierNodeElement<IndicationModifierNode>() {
//    override fun create(): IndicationModifierNode {
//        return IndicationModifierNode(indication.create(interactionSource))
//    }
//
//    override fun InspectorInfo.inspectableProperties() {
//        name = "indication"
//        properties["interactionSource"] = interactionSource
//        properties["indication"] = indication
//    }
//
//    override fun update(node: IndicationModifierNode) {
//        node.update(indication.create(interactionSource))
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is IndicationModifierElement) return false
//
//        if (interactionSource != other.interactionSource) return false
//        if (indication != other.indication) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = interactionSource.hashCode()
//        result = 31 * result + indication.hashCode()
//        return result
//    }
//}
//
///**
// * Wrapper [DelegatableNode] that allows us to replace the wrapped node fully when a new node is
// * provided.
// */
//private class IndicationModifierNode(
//    private var indicationNode: DelegatableNode
//) : DelegatingNode() {
//
//    init {
//        delegate(indicationNode)
//    }
//
//    fun update(indicationNode: DelegatableNode) {
//        undelegate(this.indicationNode)
//        this.indicationNode = indicationNode
//        delegate(indicationNode)
//    }
//}

// Draws a selection color box underneath the content
class FocusSelectionIndication internal constructor(private val color: Color) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        FocusSelectionIndicationNode(color)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FocusSelectionIndication

        return color == other.color
    }

    override fun hashCode() = color.hashCode()
}

private class FocusSelectionIndicationNode(
    private val color: Color
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode, FocusEventModifierNode {
    private var isFocused = false

    private val colorScheme get() = currentValueOf(LocalColorScheme)

    override fun ContentDrawScope.draw() {
        if (isFocused) drawRect(if(color == Color.Unspecified) colorScheme.selection else color, size = size)
        drawContent()
    }

    override fun onFocusEvent(focusState: FocusState) {
        if (isFocused != focusState.isFocused) {
            isFocused = focusState.isFocused
            invalidateDraw()
        }
    }
}

fun Modifier.focusSelectionIndication(
    interactionSource: MutableInteractionSource,
    color: Color = Color.Unspecified,
) = this then indication(interactionSource, FocusSelectionIndication(color))

// Draws a selection color box underneath the content
class HoverSelectionIndication internal constructor() : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        HoverSelectionIndicationNode(interactionSource)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }
}

private class HoverSelectionIndicationNode(
    private val interactionSource: InteractionSource,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
    private var isHovered = false

    private val colorScheme get() = currentValueOf(LocalColorScheme)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions
                .filter { it is HoverInteraction }
                .collectLatest { interaction ->
                    isHovered = interaction is HoverInteraction.Enter
                    invalidateDraw()
                }
        }
    }

    override fun ContentDrawScope.draw() {
        if (isHovered) drawRect(colorScheme.selection, size = size)
        drawContent()
    }
}

fun Modifier.hoverIndication(
    interactionSource: MutableInteractionSource,
) = indication(interactionSource, HoverSelectionIndication())

// draws a dotted line border *over* the content
class FocusDashIndication internal constructor(private val padding: Dp) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        DashIndicationNode(interactionSource, padding)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FocusDashIndication

        return padding == other.padding
    }

    override fun hashCode(): Int = padding.hashCode()
}

private class DashIndicationNode(
    private val interactionSource: InteractionSource,
    private val padding: Dp
) : Modifier.Node(), DrawModifierNode {
    private var isFocused = false

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions
                .filter { it is FocusInteraction }
                .collectLatest { interaction ->
                    isFocused = interaction is FocusInteraction.Focus
                    invalidateDraw()
                }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (isFocused) drawDashFocus(padding)
    }
}

fun Modifier.focusDashIndication(
    interactionSource: InteractionSource,
    padding: Dp = Dp.Unspecified,
) = this.indication(interactionSource, FocusDashIndication(padding))

class PressColorIndication internal constructor(
    private val pressColor: Color,
    private val backgroundColor: Color
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        PressColorIndicationNode(interactionSource, pressColor, backgroundColor)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PressColorIndication

        if (pressColor != other.pressColor) return false
        if (backgroundColor != other.backgroundColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pressColor.hashCode()
        result = 31 * result + backgroundColor.hashCode()
        return result
    }
}

private class PressColorIndicationNode(
    private val interactionSource: InteractionSource,
    private val pressedColor: Color,
    private val backgroundColor: Color,
) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
    private var isPressed = false

    private val colorScheme get() = currentValueOf(LocalColorScheme)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions
                .filter { it is PressInteraction }
                .collectLatest { interaction ->
                    isPressed = interaction is PressInteraction.Press
                    invalidateDraw()
                }
        }
    }

    override fun ContentDrawScope.draw() {
        if (isPressed) {
            drawRect(if (pressedColor == Color.Unspecified) colorScheme.buttonShadow else pressedColor)
        } else {
            drawRect(if (backgroundColor == Color.Unspecified) colorScheme.windowFrame else backgroundColor)
        }
        drawContent()
    }
}

fun Modifier.pressColorIndication(
    interactionSource: InteractionSource,
    pressColor: Color = Color.Unspecified,
    backgroundColor: Color = Color.Unspecified,
) = this.indication(interactionSource, PressColorIndication(pressColor, backgroundColor))

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

fun Modifier.selectionBackground(enabled: Boolean = true) = composed {
    then(if (enabled) Modifier.background(Win9xTheme.colorScheme.selection) else Modifier)
}