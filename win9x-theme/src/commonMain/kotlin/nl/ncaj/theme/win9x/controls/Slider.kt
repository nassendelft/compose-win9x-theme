package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.focusDashIndication
import nl.ncaj.theme.win9x.vector.Icons
import nl.ncaj.theme.win9x.vector.SliderThumb
import nl.ncaj.theme.win9x.win9xBorder
import kotlin.math.max
import kotlin.math.roundToInt


@Stable
private class SliderState(
    step: Int,
    private val steps: Int,
    private val thumbWidth: Int,
    private val parentWidth: Int,
    private val onStep: (Int) -> Unit,
) {
    var position by mutableStateOf((((parentWidth - thumbWidth) / (steps - 1)) * step).toFloat())
        private set
    private var dragPosition = position

    private fun snapToClosestStep(point: Float): Float {
        val stepSize = (parentWidth - thumbWidth) / (steps - 1)
        if (stepSize == 0) return point
        val step = (point / stepSize).roundToInt()
        return (step * stepSize).toFloat()
    }

    fun onDragStopped() {
        val stepSize = (parentWidth - thumbWidth) / (steps - 1)
        val step = (position / stepSize).roundToInt()
        onStep(step)
    }

    fun onDrag(delta: Float) {
        dragPosition += delta
        position = snapToClosestStep(dragPosition).coerceIn(
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
        state = rememberDraggableState { delta -> state.onDrag(delta) },
        onDragStopped = { state.onDragStopped() }
    )
}

@Composable
fun Slider(
    modifier: Modifier = Modifier,
    step: Int,
    steps: Int = 10,
    onStep: (Int) -> Unit,
) {
    require(steps >= 2) { "Number of steps must be 2 or larger" }

    var parentWidth by remember { mutableIntStateOf(0) }
    var thumbWidth by remember { mutableIntStateOf(0) }

    val interactionSource = remember { MutableInteractionSource() }

    val state = remember(step, steps, onStep, parentWidth, thumbWidth) {
        SliderState(step, steps, thumbWidth, parentWidth, onStep)
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
            .focusable(interactionSource = interactionSource)
            .defaultMinSize(minWidth = 100.dp, minHeight = 36.dp)
            .focusDashIndication(interactionSource)
            .padding(4.dp),
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
                painter = rememberVectorPainter(Icons.SliderThumb),
                contentDescription = "Thumb for slider",
                modifier = Modifier
                    .wrapContentSize()
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
