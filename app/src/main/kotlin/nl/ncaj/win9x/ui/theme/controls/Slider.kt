package nl.ncaj.win9x.ui.theme.controls

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.win98Border
import kotlin.math.max
import kotlin.math.min

@Preview
@Composable
fun SliderPreview() {
    Column {
        Text("- Slider -")
        Spacer(modifier = Modifier.height(2.dp))

        Slider(modifier = Modifier, steps = 4, onStep = { Log.d("test", "$it") })
    }
}

@Composable
fun Slider(
    modifier: Modifier = Modifier,
    steps: Int = 10,
    onStep: (Int) -> Unit
) {
    require(steps >= 2) { "Number of steps must be 2 or larger" }

    var parentSize by remember { mutableStateOf(IntSize.Zero) }
    var thumbSize by remember { mutableStateOf(IntSize.Zero) }
    var currentXOffset by remember { mutableFloatStateOf(0f) }
    var thumbXOffset by remember { mutableFloatStateOf(0f) }

    fun snapToClosestStep(point: Int): Pair<Int, Int> {
        val minX = thumbSize.width / 2
        val maxX = parentSize.width - minX
        val stepSize = (maxX - minX) / (steps - 1)
        return (point / stepSize) * stepSize to point / stepSize
    }

    val dragState = rememberDraggableState { delta ->
        currentXOffset = min(
            max(currentXOffset + delta, 0f),
            (parentSize.width - (thumbSize.width / 2)).toFloat()
        )
        val (offset, step) = snapToClosestStep(currentXOffset.toInt())
        if (offset.toFloat() != thumbXOffset) onStep(step)
        thumbXOffset = offset.toFloat()
    }

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = 100.dp)
            .requiredWidthIn(min = 100.dp)
            .then(modifier)
            .requiredHeight(28.dp)
            .onSizeChanged { parentSize = it },
        propagateMinConstraints = true,
    ) {
        Box(
            modifier = Modifier
                .requiredHeight(3.dp)
                .offset(y = (-5).dp)
                .win98Border(
                    outerStartTop = Win98Theme.colorScheme.buttonShadow,
                    innerStartTop = Win98Theme.colorScheme.windowFrame,
                    innerEndBottom = Win98Theme.colorScheme.buttonFace,
                    outerEndBottom = Win98Theme.colorScheme.buttonHighlight,
                    borderWidth = Win98Theme.borderWidthPx
                )
        )
        Image(
            painterResource(R.drawable.ic_slider_thumb),
            contentDescription = "Thumb for slider",
            modifier = Modifier
                .wrapContentSize()
                .onSizeChanged { thumbSize = it }
                .padding(bottom = 6.dp)
                .offset {
                    IntOffset(
                        thumbXOffset.toInt() - (parentSize.width / 2) + (thumbSize.width / 2),
                        0
                    )
                }
                .focusable()
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = dragState
                )
        )
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val minX = thumbSize.width / 2
            val maxX = size.width.toInt() - minX
            val step = ((size.width - (minX * 2)) / (steps - 1)).toInt()

            (minX..maxX step step).forEach { x ->
                drawLine(
                    Color.Black,
                    Offset(x.toFloat(), size.height - 8),
                    Offset(x.toFloat(), size.height)
                )
            }
        }
    }
}