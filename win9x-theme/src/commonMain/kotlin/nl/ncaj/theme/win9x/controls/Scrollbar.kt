package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.checkeredBackground
import nl.ncaj.theme.win9x.vector.ArrowDown
import nl.ncaj.theme.win9x.vector.ArrowDownDisabled
import nl.ncaj.theme.win9x.vector.Icons
import nl.ncaj.theme.win9x.windowBorder
import kotlin.js.JsName
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Defines how to scroll the scrollable component and how to display a scrollbar for it.
 *
 * The values of this interface are typically in pixels, but do not have to be.
 * It's possible to create an adapter with any scroll range of `Double` values.
 */
interface ScrollbarAdapter {

    // We use `Double` values here in order to allow scrolling both very large (think LazyList with
    // millions of items) and very small (think something whose natural coordinates are less than 1)
    // content.

    /**
     * Scroll offset of the content inside the scrollable component.
     *
     * For example, a value of `100` could mean the content is scrolled by 100 pixels from the
     * start.
     */
    val scrollOffset: Double

    /**
     * The size of the scrollable content, on the scrollable axis.
     */
    val contentSize: Double

    /**
     * The size of the viewport, on the scrollable axis.
     */
    val viewportSize: Double

    val canDecreaseScroll: Boolean

    val canIncreaseScroll: Boolean

    /**
     * Instantly jump to [scrollOffset].
     *
     * @param scrollOffset target offset to jump to, value will be coerced to the valid
     * scroll range.
     */
    suspend fun scrollTo(scrollOffset: Double)

    /**
     * Scroll from the current position by the given amount of pixels.
     */
    suspend fun scrollBy(value: Float)
}

internal class LazyListScrollbarAdapter(
    private val scrollState: LazyListState
): ScrollbarAdapter {

    class VisibleLine(
        val index: Int,
        val offset: Int
    )

    override val viewportSize: Double
        get() = with(scrollState.layoutInfo) {
            if (orientation == Orientation.Vertical)
                viewportSize.height
            else
                viewportSize.width
        }.toDouble()

    //FIXME
    override val canDecreaseScroll get() = true
    override val canIncreaseScroll get() = true

    /**
     * Return the first visible line, if any.
     */
    fun firstVisibleLine(): VisibleLine? {
        val firstFloatingVisibleIndex = firstFloatingVisibleItemIndex() ?: return null
        val firstFloatingItem = scrollState.layoutInfo.visibleItemsInfo[firstFloatingVisibleIndex]
        return VisibleLine(
            index = firstFloatingItem.index,
            offset = firstFloatingItem.offset
        )
    }

    /**
     * Return the total number of lines.
     */
    fun totalLineCount(): Int = scrollState.layoutInfo.totalItemsCount

    /**
     * The sum of content padding (before+after) on the scrollable axis.
     */
    fun contentPadding(): Int = with(scrollState.layoutInfo){
        beforeContentPadding + afterContentPadding
    }

    /**
     * Scroll immediately to the given line, and offset it by [scrollOffset] pixels.
     */
    suspend fun snapToLine(lineIndex: Int, scrollOffset: Int) {
        scrollState.scrollToItem(lineIndex, scrollOffset)
    }

    /**
     * Scroll from the current position by the given amount of pixels.
     */
    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }
    /**
     * A heuristic that tries to ignore the "currently stickied" header because it breaks the other
     * computations in this adapter:
     * - The currently stickied header always appears in the list of visible items, with its
     *   regular index. This makes [firstVisibleLine] always return its index, even if the list has
     *   been scrolled far beyond it.
     * - [averageVisibleLineSize] calculates the average size in O(1) by assuming that items don't
     *   overlap, and the stickied item breaks this assumption.
     *
     * Attempts to return the index into `visibleItemsInfo` of the first non-currently-stickied (it
     * could be sticky, but not stickied to the top of the list right now) item, if there is one.
     *
     * Note that this heuristic breaks down if the sticky header covers the entire list, so that
     * it's the only visible item for some portion of the scroll range. But there's currently no
     * known better way to solve it, and it's a relatively unusual case.
     */
    private fun firstFloatingVisibleItemIndex() = with(scrollState.layoutInfo.visibleItemsInfo){
        when (size) {
            0 -> null
            1 -> 0
            else -> {
                val first = this[0]
                val second = this[1]
                // If either the indices or the offsets aren't continuous, then the first item is
                // sticky, so we return 1
                if ((first.index < second.index - 1) ||
                    (first.offset + first.size + lineSpacing > second.offset))
                    1
                else
                    0
            }
        }
    }

    /**
     * Return the average size (on the scrollable axis) of the visible lines.
     */
    fun averageVisibleLineSize(): Double = with(scrollState.layoutInfo.visibleItemsInfo){
        val firstFloatingIndex = firstFloatingVisibleItemIndex() ?: return@with 0.0
        val first = this[firstFloatingIndex]
        val last = last()
        val count = size - firstFloatingIndex
        (last.offset + last.size - first.offset - (count - 1) * lineSpacing).toDouble() / count
    }

    /**
     * The spacing between lines.
     */
    val lineSpacing: Int get() = scrollState.layoutInfo.mainAxisItemSpacing

    @JsName("averageVisibleLineSizeProperty")
    private val averageVisibleLineSize by derivedStateOf {
        if (totalLineCount() == 0)
            0.0
        else
            averageVisibleLineSize()
    }

    private val averageVisibleLineSizeWithSpacing get() = averageVisibleLineSize + lineSpacing

    override val scrollOffset: Double
        get() {
            val firstVisibleLine = firstVisibleLine()
            return if (firstVisibleLine == null)
                0.0
            else
                firstVisibleLine.index * averageVisibleLineSizeWithSpacing - firstVisibleLine.offset
        }

    override val contentSize: Double
        get() {
            val totalLineCount = totalLineCount()
            return averageVisibleLineSize * totalLineCount +
                    lineSpacing * (totalLineCount - 1).coerceAtLeast(0) +
                    contentPadding()
        }

    override suspend fun scrollTo(scrollOffset: Double) {
        val distance = scrollOffset - this.scrollOffset

        // if we scroll less than viewport we need to use scrollBy function to avoid
        // undesirable scroll jumps (when an item size is different)
        //
        // if we scroll more than viewport we should immediately jump to this position
        // without recreating all items between the current and the new position
        if (abs(distance) <= viewportSize) {
            scrollBy(distance.toFloat())
        } else {
            snapTo(scrollOffset)
        }
    }

    private suspend fun snapTo(scrollOffset: Double) {
        val scrollOffsetCoerced = scrollOffset.coerceIn(0.0, maxScrollOffset)

        val index = (scrollOffsetCoerced / averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)
            .coerceAtMost(totalLineCount() - 1)

        val offset = (scrollOffsetCoerced - index * averageVisibleLineSizeWithSpacing)
            .toInt()
            .coerceAtLeast(0)

        snapToLine(lineIndex = index, scrollOffset = offset)
    }

}

internal class ScrollableScrollbarAdapter(
    private val scrollState: ScrollState,
): ScrollbarAdapter {
    override val scrollOffset: Double get() = scrollState.value.toDouble()

    override val canDecreaseScroll by derivedStateOf { scrollState.value != 0 }
    override val canIncreaseScroll by derivedStateOf { scrollState.value != scrollState.maxValue }

    override suspend fun scrollTo(scrollOffset: Double) {
        scrollState.scrollTo(scrollOffset.roundToInt())
    }

    override suspend fun scrollBy(value: Float) {
        scrollState.scrollBy(value)
    }

    override val contentSize: Double
        get() = scrollState.maxValue + viewportSize

    override val viewportSize: Double
        get() = scrollState.viewportSize.toDouble()
}

internal val ScrollbarAdapter.maxScrollOffset: Double
    get() = (contentSize - viewportSize).coerceAtLeast(0.0)

internal class SliderAdapter(
    internal val adapter: ScrollbarAdapter,
    private val trackSize: Int,
    private val minHeight: Int,
    private val reverseLayout: Boolean,
    private val isVertical: Boolean,
) {

    private val contentSize get() = adapter.contentSize

    private val visiblePart: Double
        get() {
            val contentSize = contentSize
            return if (contentSize == 0.0)
                1.0
            else
                (adapter.viewportSize / contentSize).coerceAtMost(1.0)
        }

    val thumbSize
        get() = (trackSize * visiblePart).coerceAtLeast(minHeight.toDouble())

    private val scrollScale: Double
        get() {
            val extraScrollbarSpace = trackSize - thumbSize
            val extraContentSpace = adapter.maxScrollOffset  // == contentSize - viewportSize
            return if (extraContentSpace == 0.0) 1.0 else extraScrollbarSpace / extraContentSpace
        }

    private var rawPosition: Double
        get() = scrollScale * adapter.scrollOffset
        set(value) {
            runBlockingIfPossible {
                adapter.scrollTo(value / scrollScale)
            }
        }

    var position: Double
        get() = if (reverseLayout) trackSize - thumbSize - rawPosition else rawPosition
        set(value) {
            rawPosition = if (reverseLayout) {
                trackSize - thumbSize - value
            } else {
                value
            }
        }

    // How much of the current drag was ignored because we've reached the end of the scrollbar area
    private var unscrolledDragDistance = 0.0

    /** Called when the thumb dragging starts */
    fun onDragStarted() {
        unscrolledDragDistance = 0.0
    }

    /** Called on every movement while dragging the thumb */
    fun onDragDelta(offset: Offset) {
        val dragDelta = if (isVertical) offset.y else offset.x
        val maxScrollPosition = adapter.maxScrollOffset * scrollScale
        val currentPosition = position
        val targetPosition =
            (currentPosition + dragDelta + unscrolledDragDistance).coerceIn(0.0, maxScrollPosition)
        val sliderDelta = targetPosition - currentPosition

        // Have to add to position for smooth content scroll if the items are of different size
        position += sliderDelta

        unscrolledDragDistance += dragDelta - sliderDelta
    }
}

// Because k/js and k/wasm don't have runBlocking
internal expect fun runBlockingIfPossible(block: suspend CoroutineScope.() -> Unit)


@Composable
fun rememberScrollbarAdapter(
    scrollState: LazyListState,
): ScrollbarAdapter = remember(scrollState) {
    ScrollbarAdapter(scrollState)
}

fun ScrollbarAdapter(
    scrollState: LazyListState
): ScrollbarAdapter = LazyListScrollbarAdapter(scrollState)

@Composable
fun rememberScrollbarAdapter(
    scrollState: ScrollState,
): ScrollbarAdapter = remember(scrollState) {
    ScrollbarAdapter(scrollState)
}

fun ScrollbarAdapter(
    scrollState: ScrollState
): ScrollbarAdapter = ScrollableScrollbarAdapter(scrollState)

@Composable
fun ScrollableHost(
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    horizontalScrollbarAdapter: ScrollbarAdapter? = null,
    verticalScrollbarAdapter: ScrollbarAdapter? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
) {
    if (verticalScrollbarAdapter == null && horizontalScrollbarAdapter != null) {
        content()
        return
    }

    Layout(
        content = {
            content()
            horizontalScrollbarAdapter?.let {
                Scrollbar(
                    adapter = it,
                    isVertical = false,
                    interactionSource = interactionSource,
                    reverseLayout = reverseLayout,
                    disableArrowButtonsIfNotScrollable = false,
                )
            }
            verticalScrollbarAdapter?.let {
                Scrollbar(
                    adapter = it,
                    isVertical = true,
                    interactionSource = interactionSource,
                    reverseLayout = reverseLayout,
                    disableArrowButtonsIfNotScrollable = false,
                )
            }
        },
        modifier = modifier,
        measurePolicy = object : MeasurePolicy {
            override fun MeasureScope.measure(
                measurables: List<Measurable>,
                constraints: Constraints
            ): MeasureResult {
                val contentMeasurable = measurables[0]
                val horizontalMeasurable = if(horizontalScrollbarAdapter != null) measurables[1] else null
                val verticalMeasurable = if(horizontalScrollbarAdapter != null && verticalScrollbarAdapter != null)
                    measurables[2]
                else if(horizontalScrollbarAdapter == null && verticalScrollbarAdapter != null)
                    measurables[1]
                else null


                val maxWidth = constraints.maxWidth
                val maxHeight = constraints.maxHeight

                val showVerticalScroll = constraints.hasBoundedWidth
                        && (verticalScrollbarAdapter?.contentSize ?: 0.0) > maxWidth
                val showHorizontalScroll = constraints.hasBoundedHeight
                        && (horizontalScrollbarAdapter?.contentSize ?: 0.0) > maxHeight

                val horizontalScrollPlaceable = if (showHorizontalScroll && horizontalMeasurable != null) {
                    val offset = if (showVerticalScroll && verticalMeasurable != null)
                        verticalMeasurable.maxIntrinsicWidth(maxWidth)
                    else
                        0
                    horizontalMeasurable.measure(Constraints.fixedWidth(maxWidth - offset))
                } else null

                val verticalScrollPlaceable = if (showVerticalScroll && verticalMeasurable != null) {
                    val offset = if (showHorizontalScroll && horizontalMeasurable != null)
                        horizontalMeasurable.maxIntrinsicHeight(maxHeight)
                    else
                        0
                    verticalMeasurable.measure(Constraints.fixedHeight(maxHeight - offset))
                } else null

                val verticalWidth = verticalScrollPlaceable?.width ?: 0
                val horizontalHeight = horizontalScrollPlaceable?.height ?: 0
                val contentConstraints = Constraints.fixed(
                    width = maxWidth - verticalWidth,
                    height = maxHeight - horizontalHeight,
                )

                val contentPlaceable = contentMeasurable.measure(contentConstraints)

                val width = contentPlaceable.width + verticalWidth
                val height = contentPlaceable.height + horizontalHeight

                return layout(width, height) {
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
fun HorizontalScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    reverseLayout: Boolean = false,
    disableArrowButtonsIfNotScrollable: Boolean = true,
) = Scrollbar(
    modifier = modifier,
    adapter = ScrollableScrollbarAdapter(scrollState),
    interactionSource = interactionSource,
    reverseLayout = reverseLayout,
    isVertical = false,
    disableArrowButtonsIfNotScrollable = disableArrowButtonsIfNotScrollable,
)

@Composable
fun VerticalScrollBar(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    reverseLayout: Boolean = false,
    disableArrowButtonsIfNotScrollable: Boolean = true,
) = Scrollbar(
    modifier = modifier,
    adapter = ScrollableScrollbarAdapter(scrollState),
    interactionSource = interactionSource,
    reverseLayout = reverseLayout,
    isVertical = true,
    disableArrowButtonsIfNotScrollable = disableArrowButtonsIfNotScrollable,
)

@Composable
private fun Scrollbar(
    modifier: Modifier = Modifier,
    adapter: ScrollbarAdapter,
    interactionSource: MutableInteractionSource,
    reverseLayout: Boolean,
    isVertical: Boolean,
    disableArrowButtonsIfNotScrollable: Boolean,
) = with(LocalDensity.current) {
    val scope = rememberCoroutineScope()

    val dragInteraction = remember { mutableStateOf<DragInteraction.Start?>(null) }
    DisposableEffect(interactionSource) {
        onDispose {
            dragInteraction.value?.let { interaction ->
                interactionSource.tryEmit(DragInteraction.Cancel(interaction))
                dragInteraction.value = null
            }
        }
    }

    var containerSize by remember { mutableStateOf(0) }

    val minimalHeight = 15.dp.roundToPx()

    val sliderAdapter = remember(adapter, containerSize, minimalHeight, reverseLayout, isVertical) {
        SliderAdapter(adapter, containerSize, minimalHeight, reverseLayout, isVertical)
    }

    val scrollThickness = 15.dp.roundToPx()

    val measurePolicy = if (isVertical) {
        remember(sliderAdapter, scrollThickness) {
            verticalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    } else {
        remember(sliderAdapter, scrollThickness) {
            horizontalMeasurePolicy(sliderAdapter, { containerSize = it }, scrollThickness)
        }
    }

    val scroller = rememberScroller(sliderAdapter, scrollThickness)

    Layout(
        modifier = modifier
            .checkeredBackground(
                Win9xTheme.colorScheme.buttonFace,
                Win9xTheme.colorScheme.buttonHighlight,
            )
            .then(
                if(scroller.trackPressed) Modifier.background(Color.Black.copy(alpha = 0.4f))
                else Modifier
            )
            .scrollOnPressTrack(isVertical, scroller),
        content = {
            // decrease button
            Button(
                onClick = { scope.launch { adapter.scrollBy(-10f) } },
                borders = innerButtonBorders(),
                enabled = !disableArrowButtonsIfNotScrollable || adapter.canDecreaseScroll
            ) {
                val image = if (disableArrowButtonsIfNotScrollable && !adapter.canDecreaseScroll) {
                    rememberVectorPainter(Icons.ArrowDownDisabled)
                } else {
                    rememberVectorPainter(Icons.ArrowDown)
                }
                Image(
                    painter = image,
                    contentDescription = "Decrease scrolling",
                    modifier = Modifier.rotate(if (isVertical) 180f else 90f)
                )
            }

            // slider
            Box(
                modifier = Modifier
                    .background(Win9xTheme.colorScheme.buttonFace)
                    .windowBorder()
                    .scrollbarDrag(
                        interactionSource = interactionSource,
                        draggedInteraction = dragInteraction,
                        sliderAdapter = sliderAdapter,
                    )
            )

            // increase button
            Button(
                onClick = { scope.launch { adapter.scrollBy(10f) } },
                borders = innerButtonBorders(),
                enabled = !disableArrowButtonsIfNotScrollable || adapter.canIncreaseScroll
            ) {
                val image = if (disableArrowButtonsIfNotScrollable && !adapter.canIncreaseScroll) {
                    rememberVectorPainter(Icons.ArrowDownDisabled)
                } else {
                    rememberVectorPainter(Icons.ArrowDown)
                }
                Image(
                    painter = image,
                    contentDescription = "Increase scrolling",
                    modifier = Modifier.rotate(if (isVertical) 0f else -90f)
                )
            }
        },
        measurePolicy = measurePolicy
    )
}

private fun horizontalMeasurePolicy(
    adapter: SliderAdapter,
    setContainerSize: (Int) -> Unit,
    sliderSize: Int,
) = object : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        setContainerSize(constraints.maxWidth)
        val decreasePlaceable = measurables[0].measure(Constraints.fixed(sliderSize, sliderSize))
        val increasePlaceable = measurables[2].measure(Constraints.fixed(sliderSize, sliderSize))
        setContainerSize(constraints.maxWidth - decreasePlaceable.width - increasePlaceable.width)

        val pixelRange = thumbPixelRange(adapter, sliderSize)
        val sliderPlaceable = measurables[1].measure(
            Constraints.fixed(
                pixelRange.size,
                constraints.constrainHeight(sliderSize)
            )
        )

        return layout(constraints.maxWidth, sliderPlaceable.height) {
            decreasePlaceable.place(0, 0)
            sliderPlaceable.place(pixelRange.first, 0)
            increasePlaceable.place(constraints.maxWidth - increasePlaceable.width, 0)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurables: List<IntrinsicMeasurable>,
        width: Int
    ) = sliderSize
}

private fun verticalMeasurePolicy(
    sliderAdapter: SliderAdapter,
    setContainerSize: (Int) -> Unit,
    sliderSize: Int
) = object : MeasurePolicy {
    override fun MeasureScope.measure(
        measurables: List<Measurable>,
        constraints: Constraints
    ): MeasureResult {
        val decreasePlaceable = measurables[0].measure(Constraints.fixed(sliderSize, sliderSize))
        val increasePlaceable = measurables[2].measure(Constraints.fixed(sliderSize, sliderSize))
        setContainerSize(constraints.maxHeight - decreasePlaceable.height - increasePlaceable.height)

        val pixelRange = thumbPixelRange(sliderAdapter, sliderSize)
        val placeable = measurables[1].measure(
            Constraints.fixed(
                constraints.constrainWidth(sliderSize),
                pixelRange.size
            )
        )

        return layout(placeable.width, constraints.maxHeight) {
            decreasePlaceable.place(0, 0)
            placeable.place(0, pixelRange.first)
            increasePlaceable.place(0, constraints.maxHeight - increasePlaceable.height)
        }
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurables: List<IntrinsicMeasurable>,
        height: Int
    ) = sliderSize
}

private val IntRange.size get() = last + 1 - first

private fun thumbPixelRange(sliderAdapter: SliderAdapter, sliderSize: Int): IntRange {
    val start = sliderAdapter.position.roundToInt() + sliderSize
    val endExclusive = start + sliderAdapter.thumbSize.roundToInt()

    return start until endExclusive
}

private fun Modifier.scrollbarDrag(
    interactionSource: MutableInteractionSource,
    draggedInteraction: MutableState<DragInteraction.Start?>,
    sliderAdapter: SliderAdapter,
): Modifier = composed {
    val currentInteractionSource by rememberUpdatedState(interactionSource)
    val currentDraggedInteraction by rememberUpdatedState(draggedInteraction)
    val currentSliderAdapter by rememberUpdatedState(sliderAdapter)

    pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val interaction = DragInteraction.Start()
            currentInteractionSource.tryEmit(interaction)
            currentDraggedInteraction.value = interaction
            currentSliderAdapter.onDragStarted()
            val isSuccess = drag(down.id) { change ->
                currentSliderAdapter.onDragDelta(change.positionChange())
                change.consume()
            }
            val finishInteraction = if (isSuccess) {
                DragInteraction.Stop(interaction)
            } else {
                DragInteraction.Cancel(interaction)
            }
            currentInteractionSource.tryEmit(finishInteraction)
            currentDraggedInteraction.value = null
        }
    }
}

private fun Modifier.scrollOnPressTrack(
    isVertical: Boolean,
    scroller: Scroller,
) = pointerInput(scroller) {
    detectScrollViaTrackGestures(
        isVertical = isVertical,
        scroller = scroller
    )
}

@Composable
private fun rememberScroller(
    sliderAdapter: SliderAdapter,
    sliderSize: Int,
): Scroller {
    val coroutineScope = rememberCoroutineScope()
    return remember(sliderAdapter, coroutineScope) {
        Scroller(coroutineScope, sliderAdapter, sliderSize)
    }
}

private class Scroller(
    private val coroutineScope: CoroutineScope,
    private val sliderAdapter: SliderAdapter,
    private val sliderSize: Int
) {

    /**
     * The current direction of scroll (1: down/right, -1: up/left, 0: not scrolling)
     */
    private var direction = 0

    /**
     * The currently pressed location (in pixels) on the scrollable axis.
     */
    private var offset: Float? = null

    /**
     * The job that keeps scrolling while the track is pressed.
     */
    private var job: Job? = null

    var trackPressed by mutableStateOf(false)

    /**
     * Calculates the direction of scrolling towards the given offset (in pixels).
     */
    private fun directionOfScrollTowards(offset: Float): Int {
        val pixelRange = thumbPixelRange(sliderAdapter, sliderSize)
        return when {
            offset < pixelRange.first -> -1
            offset > pixelRange.last -> 1
            else -> 0
        }
    }

    /**
     * Scrolls once towards the current offset, if it matches the direction of the current gesture.
     */
    private suspend fun scrollTowardsCurrentOffset() {
        offset?.let {
            val currentDirection = directionOfScrollTowards(it)
            if (currentDirection != direction) return
            with(sliderAdapter.adapter) {
                scrollTo(scrollOffset + currentDirection * viewportSize)
            }
        }
    }

    /**
     * Starts the job that scrolls continuously towards the current offset.
     */
    private fun startScrolling() {
        job?.cancel()
        job = coroutineScope.launch {
            scrollTowardsCurrentOffset()
            delay(DelayBeforeSecondScrollOnTrackPress)
            while (true) {
                scrollTowardsCurrentOffset()
                delay(DelayBetweenScrollsOnTrackPress)
            }
        }
    }

    private fun offsetIsOnThumb(offset: Float): Boolean {
        val pixelRange = thumbPixelRange(sliderAdapter, sliderSize)
        return offset.roundToInt() in pixelRange
    }

    /**
     * Invoked on the first press for a gesture.
     */
    fun onPress(offset: Float) {
        this.offset = offset
        this.direction = directionOfScrollTowards(offset)

        if (direction != 0) {
            if (!offsetIsOnThumb(offset)) trackPressed = true
            startScrolling()
        }
    }

    /**
     * Invoked when the pointer moves while pressed during the gesture.
     */
    fun onMovePressed(offset: Float) {
        this.offset = offset
    }

    /**
     * Cleans up when the gesture finishes.
     */
    private fun cleanupAfterGesture() {
        job?.cancel()
        direction = 0
        offset = null
        trackPressed = false
    }

    /**
     * Invoked when the button is released.
     */
    fun onRelease() {
        cleanupAfterGesture()
    }

    /**
     * Invoked when the gesture is cancelled.
     */
    fun onGestureCancelled() {
        cleanupAfterGesture()
        // Maybe revert to the initial position?
    }

}

/**
 * Detects the pointer events relevant for the "scroll by pressing on the track outside the thumb"
 * gesture and calls the corresponding methods in the [scroller].
 */
private suspend fun PointerInputScope.detectScrollViaTrackGestures(
    isVertical: Boolean,
    scroller: Scroller,
) {
    fun Offset.onScrollAxis() = if (isVertical) y else x

    awaitEachGesture {
        val down = awaitFirstDown()
        scroller.onPress(down.position.onScrollAxis())

        while (true) {
            val drag = if (isVertical) awaitVerticalDragOrCancellation(down.id)
            else awaitHorizontalDragOrCancellation(down.id)

            if (drag == null) {
                scroller.onGestureCancelled()
                break
            } else if (!drag.pressed) {
                scroller.onRelease()
                break
            } else
                scroller.onMovePressed(drag.position.onScrollAxis())
        }
    }
}

/**
 * The delay between the 1st and 2nd scroll while the scrollbar track is pressed outside the thumb.
 */
internal const val DelayBeforeSecondScrollOnTrackPress: Long = 300L

/**
 * The delay between each subsequent (after the 2nd) scroll while the scrollbar track is pressed
 * outside the thumb.
 */
internal const val DelayBetweenScrollsOnTrackPress: Long = 100L
