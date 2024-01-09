package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.win98Border

@Composable
@Preview
fun ProgressIndicatorPreview() {
    Column {
        Text("- Progress Indicator -")
        Spacer(modifier = Modifier.height(2.dp))

        ProgressIndicator()
    }
}

@Composable
fun ProgressIndicator(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .defaultMinSize(100.dp, 20.dp)
            .background(Win98Theme.colorScheme.buttonFace)
            .progressIndicatorBorder()
            .padding(Win98Theme.borderWidthDp + 1.dp)
            .progressIndicator(0.5f)
    )
}

internal fun Modifier.progressIndicatorBorder() = composed {
    win98Border(
        outerStartTop = Win98Theme.colorScheme.buttonShadow,
        outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
        borderWidth = Win98Theme.borderWidthPx,
    )
}

private fun Modifier.progressIndicator(progress: Float) = composed {
    val progressColor = Win98Theme.colorScheme.selection
    this.then(
        ProgressIndicatorElement(
            color = progressColor,
            progress = progress,
            inspectorInfo = {
                debugInspectorInfo {
                    name = "ProgressIndicator"
                    properties["progress"] = progress
                }
            }
        )
    )
}

private class ProgressIndicatorElement(
    private val color: Color,
    private val progress: Float,
    private val inspectorInfo: InspectorInfo.() -> Unit
) : ModifierNodeElement<ProgressIndicatorNode>() {
    override fun create() = ProgressIndicatorNode(color, progress)

    override fun update(node: ProgressIndicatorNode) {
        node.color = color
        node.progress = progress
    }

    override fun InspectorInfo.inspectableProperties() = inspectorInfo()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProgressIndicatorElement

        if (color != other.color) return false
        if (progress != other.progress) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + progress.hashCode()
        return result
    }
}

private class ProgressIndicatorNode(
    var color: Color,
    var progress: Float
) : DrawModifierNode, Modifier.Node() {

    override fun ContentDrawScope.draw() {
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width * progress, size.height / 2),
            strokeWidth = size.height,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 4f), 1f)
        )
        drawContent()
    }
}
