package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.rememberVectorResourcePainter
import nl.ncaj.win9x.ui.theme.win9xBorder
import kotlin.math.max
import kotlin.math.roundToInt


@Stable
private class SliderState(
    private val steps: Int,
    private val thumbWidth: Int,
    private val parentWidth: Int,
    private val onStep: (Int) -> Unit,
) {
    var position by mutableStateOf(0f)
    private var dragPosition = 0f

    private fun snapToClosestStep(point: Float): Pair<Float, Int> {
        val stepSize = (parentWidth - thumbWidth) / (steps - 1)
        if (stepSize == 0) return point to 0
        val step = (point / stepSize).roundToInt()
        return (step * stepSize).toFloat() to step
    }

    fun updatePosition(delta: Float) {
        dragPosition += delta
        val (offset, step) = snapToClosestStep(dragPosition)
        if (offset != position) onStep(step)
        position = offset.coerceIn(
            minimumValue = 0f,
            maximumValue = parentWidth - thumbWidth.toFloat()
        )
    }
}

private fun Modifier.thumbDrag(
    state: SliderState,
) = composed {
    this.draggable(
        orientation = Orientation.Horizontal,
        state = rememberDraggableState { delta -> state.updatePosition(delta) }
    )
}

@Composable
fun Slider(
    modifier: Modifier = Modifier,
    steps: Int = 10,
    onStep: (Int) -> Unit,
) {
    require(steps >= 2) { "Number of steps must be 2 or larger" }

    var parentWidth by remember { mutableIntStateOf(0) }
    var thumbWidth by remember { mutableIntStateOf(0) }

    val state = remember(steps, onStep, parentWidth, thumbWidth) {
        SliderState(steps, thumbWidth, parentWidth, onStep)
    }

    val policy = remember(state) {
        MeasurePolicy { measurables, constraints ->
            val trackPlaceable = measurables[0].measure(Constraints.fixedWidth(constraints.minWidth))
            val thumbPlaceable = measurables[1].measure(Constraints())
            val stepsPlaceable = measurables[2].measure(Constraints.fixedWidth(constraints.minWidth))

            parentWidth = constraints.minWidth
            thumbWidth = thumbPlaceable.width

            val height = constraints.minHeight
            layout(constraints.minWidth, height) {
                trackPlaceable.place(0, 15)
                thumbPlaceable.place(state.position.roundToInt(), 0)
                stepsPlaceable.place(0, height - stepsPlaceable.height)
            }
        }
    }

    Layout(
        modifier = modifier
            .defaultMinSize(minWidth = 100.dp, minHeight = 28.dp),
        content = {
            // track
            Box(
                modifier = Modifier
                    .requiredHeight(3.dp)
                    .win9xBorder(
                        outerStartTop = Win9xTheme.colorScheme.buttonShadow,
                        innerStartTop = Win9xTheme.colorScheme.windowFrame,
                        innerEndBottom = Win9xTheme.colorScheme.buttonFace,
                        outerEndBottom = Win9xTheme.colorScheme.buttonHighlight,
                        borderWidth = Win9xTheme.borderWidthPx
                    )
            )

            // thumb
            Image(
                painter = rememberVectorResourcePainter("vector_images/ic_slider_thumb.xml"),
                contentDescription = "Thumb for slider",
                modifier = Modifier
                    .wrapContentSize()
                    .focusable()
                    .thumbDrag(state)
            )

            //  steps
            Canvas(Modifier.height(4.dp)) {
                val minX = thumbWidth / 2
                val maxX = size.width.toInt() - minX
                val step = max(((size.width - (minX * 2)) / (steps - 1)).toInt(), 1)

                (minX..maxX step step).forEach { x ->
                    drawLine(
                        Color.Black,
                        Offset(x.toFloat(), 0f),
                        Offset(x.toFloat(), size.height)
                    )
                }
            }
        },
        measurePolicy = policy
    )
}
