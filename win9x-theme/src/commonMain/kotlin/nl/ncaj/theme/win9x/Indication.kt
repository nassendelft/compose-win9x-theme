package nl.ncaj.theme.win9x

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.*
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch


// we use our own `indication` function, as the default one filters out focus states, which we don't want.
internal fun Modifier.indication(
    interactionSource: InteractionSource,
    indication: IndicationNodeFactory
) = this.then(IndicationModifierElement(interactionSource, indication))

/**
 * ModifierNodeElement to create [IndicationNodeFactory] instances. More complicated modifiers such
 * as [clickable] should manually delegate to the node returned by [IndicationNodeFactory]
 * internally.
 */
private class IndicationModifierElement(
    private val interactionSource: InteractionSource,
    private val indication: IndicationNodeFactory
) : ModifierNodeElement<IndicationModifierNode>() {
    override fun create(): IndicationModifierNode {
        return IndicationModifierNode(indication.create(interactionSource))
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "indication"
        properties["interactionSource"] = interactionSource
        properties["indication"] = indication
    }

    override fun update(node: IndicationModifierNode) {
        node.update(indication.create(interactionSource))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IndicationModifierElement) return false

        if (interactionSource != other.interactionSource) return false
        if (indication != other.indication) return false

        return true
    }

    override fun hashCode(): Int {
        var result = interactionSource.hashCode()
        result = 31 * result + indication.hashCode()
        return result
    }
}

/**
 * Wrapper [DelegatableNode] that allows us to replace the wrapped node fully when a new node is
 * provided.
 */
private class IndicationModifierNode(
    private var indicationNode: DelegatableNode
) : DelegatingNode() {

    init {
        delegate(indicationNode)
    }

    fun update(indicationNode: DelegatableNode) {
        undelegate(this.indicationNode)
        this.indicationNode = indicationNode
        delegate(indicationNode)
    }
}

// Draws a selection color box underneath the content
class FocusSelectionIndication private constructor(private val color: Color) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        FocusSelectionIndicationNode(interactionSource, color)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FocusSelectionIndication

        return color == other.color
    }

    override fun hashCode() = color.hashCode()

    private class FocusSelectionIndicationNode(
        private val interactionSource: InteractionSource,
        private val color: Color
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
            if (isFocused) drawRect(color, size = size)
            drawContent()
        }
    }

    companion object {
        @Composable
        fun Modifier.focusSelectionIndication(
            interactionSource: MutableInteractionSource,
            color: Color = Win9xTheme.colorScheme.selection,
        ) = this then indication(interactionSource, FocusSelectionIndication(color))

        @Composable
        fun create(
            color: Color = Win9xTheme.colorScheme.selection
        ) = FocusSelectionIndication(color)
    }
}


// Draws a selection color box underneath the content
fun Modifier.hoverSelection(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
) = hoverable(interactionSource, enabled)
    .indication(interactionSource, HoverSelectionIndication())

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

    private class HoverSelectionIndicationNode(
        private val interactionSource: InteractionSource,
    ) : Modifier.Node(), DrawModifierNode, CompositionLocalConsumerModifierNode {
        private var isHovered = false

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
            val color = currentValueOf(LocalColorScheme).selection
            if (isHovered) drawRect(color, size = size)
            drawContent()
        }
    }
}

// draws a dotted line border *over* the content
class FocusDashIndication(
    private val padding: Dp = Dp.Unspecified
) : IndicationNodeFactory {

    override fun create(interactionSource: InteractionSource): DelegatableNode =
        DashIndicationNode(interactionSource, padding)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as FocusDashIndication

        return padding == other.padding
    }

    override fun hashCode(): Int = padding.hashCode()

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

    companion object {
        fun Modifier.focusDashIndication(
            interactionSource: InteractionSource,
            padding: Dp = Dp.Unspecified,
        ) = this.indication(interactionSource, FocusDashIndication(padding))

        val FocusDashIndicationNoPadding = FocusDashIndication()
    }
}

class PressColorIndication private constructor(
    private val pressColor: Color = Color.Unspecified,
    private val backgroundColor: Color = Color.Unspecified
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

    private class PressColorIndicationNode(
        private val interactionSource: InteractionSource,
        private val pressedColor: Color,
        private val backgroundColor: Color,
    ) : Modifier.Node(), DrawModifierNode {
        private var isPressed = false

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
            if (isPressed) drawRect(pressedColor)
            else drawRect(backgroundColor)
            drawContent()
        }
    }

    companion object {
        @Composable
        fun Modifier.pressColorIndication(
            interactionSource: InteractionSource,
            pressColor: Color = Win9xTheme.colorScheme.buttonShadow,
            backgroundColor: Color = Win9xTheme.colorScheme.windowFrame
        ) = this.indication(interactionSource, PressColorIndication(pressColor, backgroundColor))
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

fun Modifier.selectionBackground(enabled: Boolean = true) = composed {
    then(if (enabled) Modifier.background(Win9xTheme.colorScheme.selection) else Modifier)
}