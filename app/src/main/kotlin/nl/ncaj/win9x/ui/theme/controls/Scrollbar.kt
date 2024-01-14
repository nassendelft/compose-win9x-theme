package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.checkeredBackground
import nl.ncaj.win9x.ui.theme.sunkenBorder
import nl.ncaj.win9x.ui.theme.windowBorder
import kotlin.math.max
import kotlin.math.roundToInt

@Preview
@Composable
fun ScrollbarPreview() {
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column {
        Text("- Scroll bars -")
        Spacer(modifier = Modifier.height(2.dp))

        ScrollableHost(
            modifier = Modifier
                .sizeIn(maxHeight = 150.dp, maxWidth = 150.dp)
                .sunkenBorder()
                .padding(Win98Theme.borderWidthDp),
            horizontalScrollState,
            verticalScrollState,
        ) {
            Text(
                text = "Some text that is repeated multiple times\n".repeat(10),
                modifier = Modifier
                    .background(Win98Theme.colorScheme.buttonHighlight)
                    .padding(4.dp)
                    .horizontalScroll(horizontalScrollState)
                    .verticalScroll(verticalScrollState)
            )
        }
    }
}

@Composable
fun ScrollableHost(
    modifier: Modifier = Modifier,
    horizontalScrollState: ScrollState,
    verticalScrollState: ScrollState,
    content: @Composable () -> Unit,
) {
    Layout(
        content = {
            content()
            HorizontalScrollbar(scrollState = horizontalScrollState)
            VerticalScrollbar(scrollState = verticalScrollState)
        },
        modifier = modifier,
        measurePolicy = object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                val contentMeasurable = measurables[0]
                val horizontalMeasurable = measurables[1]
                val verticalMeasurable = measurables[2]

                val maxWidth = constraints.maxWidth
                val maxHeight = constraints.maxHeight

                val showVerticalScroll = constraints.hasBoundedWidth
                        && contentMeasurable.maxIntrinsicHeight(maxWidth) > maxWidth
                val showHorizontalScroll = constraints.hasBoundedHeight
                        && contentMeasurable.maxIntrinsicWidth(maxHeight) > maxHeight

                val horizontalScrollPlaceable = if (showHorizontalScroll) {
                    val offset =
                        if (showVerticalScroll) verticalMeasurable.maxIntrinsicWidth(maxWidth)
                        else 0
                    horizontalMeasurable.measure(constraints.copy(maxWidth = maxWidth - offset))
                } else null

                val verticalScrollPlaceable = if (showVerticalScroll) {
                    val offset =
                        if (showHorizontalScroll) horizontalMeasurable.maxIntrinsicHeight(maxHeight)
                        else 0
                    verticalMeasurable.measure(constraints.copy(maxHeight = maxHeight - offset))
                } else null

                val verticalWidth = verticalScrollPlaceable?.width ?: 0
                val horizontalHeight = horizontalScrollPlaceable?.height ?: 0
                val contentConstraints = constraints.copy(
                    maxWidth = maxWidth - verticalWidth,
                    maxHeight = maxHeight - horizontalHeight,
                )

                val contentPlaceable = contentMeasurable.measure(contentConstraints)

                return layout(
                    width = contentPlaceable.width + verticalWidth,
                    height = contentPlaceable.height + horizontalHeight
                ) {
                    contentPlaceable.place(0, 0)
                    verticalScrollPlaceable?.place(contentPlaceable.width, 0)
                    horizontalScrollPlaceable?.place(0, contentPlaceable.height)
                }
            }

            override fun IntrinsicMeasureScope.maxIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int
            ) = measurables[0].maxIntrinsicHeight(width)

            override fun IntrinsicMeasureScope.maxIntrinsicWidth(
                measurables: List<IntrinsicMeasurable>,
                height: Int
            ) = measurables[0].maxIntrinsicWidth(height)

            override fun IntrinsicMeasureScope.minIntrinsicHeight(
                measurables: List<IntrinsicMeasurable>,
                width: Int
            ) = measurables[0].minIntrinsicHeight(width)

            override fun IntrinsicMeasureScope.minIntrinsicWidth(
                measurables: List<IntrinsicMeasurable>,
                height: Int
            ) = measurables[0].minIntrinsicHeight(height)
        }
    )
}

@Composable
fun HorizontalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    var trackSize by remember { mutableStateOf(IntSize.Zero) }
    var thumbSize by remember { mutableStateOf(IntSize.Zero) }
    val coroutineScope = rememberCoroutineScope()

    val maxScrollAmount = trackSize.width - thumbSize.width
    val thumbOffset = ((scrollState.value.toFloat() / scrollState.maxValue) * maxScrollAmount)
        .toInt()
        .coerceIn(0, maxScrollAmount)

    fun scrollByThumb(delta: Float, maxScrollAmount: Int) {
        coroutineScope.launch {
            val value = ((delta + thumbOffset) / maxScrollAmount) * scrollState.maxValue
            scrollState.scrollTo(value.roundToInt())
        }
    }

    Row(
        modifier = modifier
            .requiredHeight(15.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch { scrollState.scrollTo(scrollState.value - 10) }
            },
            borders = innerButtonBorders(),
            modifier = Modifier.width(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "",
                modifier = Modifier.rotate(90f)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .onSizeChanged { trackSize = it }
                .checkeredBackground(
                    Win98Theme.colorScheme.buttonFace,
                    Win98Theme.colorScheme.buttonHighlight,
                ),
        ) {
            Box(
                modifier
                    .fillMaxHeight()
                    .offset { IntOffset(x = thumbOffset, y = 0) }
                    .background(Win98Theme.colorScheme.buttonFace)
                    .width(40.dp)
                    .onSizeChanged { thumbSize = it }
                    .windowBorder()
                    .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { scrollByThumb(it, maxScrollAmount) }
                    )
            )
        }
        Button(
            onClick = {
                coroutineScope.launch { scrollState.scrollTo(scrollState.value + 10) }
            },
            borders = innerButtonBorders(),
            modifier = Modifier.width(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "",
                modifier = Modifier.rotate(-90f)
            )
        }
    }
}


@Composable
fun VerticalScrollbar(
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    var trackSize by remember { mutableStateOf(IntSize.Zero) }
    var thumbSize by remember { mutableStateOf(IntSize.Zero) }
    val coroutineScope = rememberCoroutineScope()

    val maxScrollAmount = max(trackSize.height - thumbSize.height, 0)
    val thumbOffset = ((scrollState.value.toFloat() / scrollState.maxValue) * maxScrollAmount)
        .toInt()
        .coerceIn(0, maxScrollAmount)

    fun scrollByThumb(delta: Float, maxScrollAmount: Int) {
        coroutineScope.launch {
            val value = ((delta + thumbOffset) / maxScrollAmount) * scrollState.maxValue
            scrollState.scrollTo(value.roundToInt())
        }
    }

    Column(
        modifier = modifier
            .requiredWidth(15.dp)
    ) {
        Button(
            onClick = {
                coroutineScope.launch { scrollState.scrollTo(scrollState.value - 10) }
            },
            borders = innerButtonBorders(),
            modifier = Modifier.requiredHeight(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = "",
                modifier = Modifier.rotate(180f)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onSizeChanged { trackSize = it }
                .checkeredBackground(
                    Win98Theme.colorScheme.buttonFace,
                    Win98Theme.colorScheme.buttonHighlight,
                ),
        ) {
            Box(
                modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = thumbOffset) }
                    .background(Win98Theme.colorScheme.buttonFace)
                    .requiredHeight(40.dp)
                    .onSizeChanged { thumbSize = it }
                    .windowBorder()
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { scrollByThumb(it, maxScrollAmount) }
                    )
            )
        }
        Button(
            onClick = {
                coroutineScope.launch { scrollState.scrollTo(scrollState.value + 10) }
            },
            borders = innerButtonBorders(),
            modifier = Modifier.requiredHeight(15.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down),
                contentDescription = ""
            )
        }
    }
}
