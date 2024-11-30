package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.DashFocusIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder


internal class TreeViewItem(
    val key: Any,
    val depth: Int,
    val lineIndices: List<Int>,
    val children: (TreeViewScope.() -> Unit)?,
    val content: @Composable () -> Unit
)

class TreeViewScope internal constructor(
    private val depth: Int = 0,
    private val lineIndices: List<Int> = emptyList(),
) {
    internal val items = mutableListOf<TreeViewItem>()

    fun item(
        key: Any,
        children: (TreeViewScope.() -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        val item = TreeViewItem(key, depth, lineIndices, children, content)
        items.add(item)
    }

    internal fun newTreeViewScope(item: TreeViewItem) = item.children?.let { children ->
        val isLastItem = items.indexOf(item) == items.size - 1
        val indices = if (isLastItem) lineIndices.filterNot { depth - 1 == it } else lineIndices
        TreeViewScope(depth = depth + 1, lineIndices = indices + depth).apply(children)
    }
}

@Composable
fun TreeViewItem(
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: (() -> Unit)? = null,
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = modifier.then(
            if (onClick == null) Modifier
            else {
                Modifier.clickable(
                    enabled = enabled,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = onClick
                )
            }
        )
    ) {
        leadingIcon?.let {
            Box(
                modifier = Modifier.size(17.dp),
                content = { it.invoke() },
                contentAlignment = Alignment.Center
            )
            Spacer(Modifier.width(4.dp))
        }

        val textStyle = when {
            !enabled -> Win9xTheme.typography.disabled
            isFocused -> Win9xTheme.typography.caption
            else -> Win9xTheme.typography.default
        }

        Text(
            text = label,
            style = textStyle,
            modifier = Modifier
                .then(
                    if (isFocused) Modifier.background(Win9xTheme.colorScheme.selection)
                    else Modifier
                )
                .focusable(enabled, interactionSource)
                .indication(
                    indication = DashFocusIndication.DashFocusIndicationNoPadding,
                    interactionSource = interactionSource
                )
                .padding(horizontal = 1.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun TreeView(
    modifier: Modifier = Modifier,
    collapsable: Boolean = true,
    showRelationship: Boolean = true,
    content: TreeViewScope.() -> Unit
) {
    val lazyListState = rememberLazyListState()
    val horizontalScroll = rememberScrollState()
    ScrollableHost(
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScroll),
        verticalScrollbarAdapter = rememberScrollbarAdapter(lazyListState),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        TreeViewContent(
            content = content,
            collapsable = collapsable,
            showRelationship = showRelationship,
            state = lazyListState,
            modifier = modifier
                .background(Win9xTheme.colorScheme.buttonHighlight)
                .horizontalScroll(horizontalScroll)
        )
    }
}

private fun createFlatTreeViewItems(
    rootScope: TreeViewScope,
    collapsedItems: List<Any>
): MutableList<TreeViewItem> {
    val result = mutableListOf<TreeViewItem>()
    val stack = mutableListOf<Pair<TreeViewItem, TreeViewScope>>()

    stack.addAll(rootScope.items.reversed().map { it to rootScope })

    while (stack.isNotEmpty()) {
        val (current, scope) = stack.removeLastOrNull() ?: continue

        result.add(current)

        if (current.children != null && !collapsedItems.contains(current.key)) {
            scope.newTreeViewScope(current)
                ?.let { childScope -> stack.addAll(childScope.items.reversed().map { it to childScope }) }
        }
    }

    return result
}

@Composable
private fun TreeViewContent(
    modifier: Modifier = Modifier,
    collapsable: Boolean = true,
    showRelationship: Boolean = true,
    state: LazyListState = rememberLazyListState(),
    content: TreeViewScope.() -> Unit
) {
    val depthInset = 20.dp

    val collapsedItems = remember { mutableStateListOf<Any>() }
    val interactionSource = remember { MutableInteractionSource() }

    val root = remember(content) { TreeViewScope().apply(content) }
    val items by remember (root, collapsedItems) { derivedStateOf { createFlatTreeViewItems(root, collapsedItems) } }

    LazyColumn(
        modifier = modifier,
        state = state,
    ) {
        itemsIndexed(items, key = {_, item -> item.key }) { itemIndex, item ->
            // If there's no depth (the root children have no children of its own) then there's need to draw
            // the expand toggle and thus no need to reserve any spacing at all
            val initialDepthOffset =
                if (!collapsable || (root.items.all { it.children == null })) 0.dp
                else depthInset

            Box(Modifier.height(IntrinsicSize.Min)) {
                // draws relationship lines
                if (showRelationship) {
                    // draws vertical lines for each parent (to the root) that still has siblings
                    item.lineIndices.forEachIndexed { index, lineIndex ->
                        // since this is a flat list of a hierarchical structure, we can check the next
                        // index and see if it's on the same depth, if it's not than we are the last item on this
                        // subtree.
                        val isLastItem = items.getOrNull(itemIndex + 1)?.depth != item.depth
                        // vertical lines should normally be drawn from top to bottom, but if it's the last line
                        // we need to only draw half.
                        val heightFraction = if (isLastItem && index == item.lineIndices.size - 1) 0.5f else 1f
                        val startPadding = (depthInset * lineIndex) + initialDepthOffset
                        DashedVerticalLine(
                            modifier = Modifier.padding(start = startPadding)
                                .width(20.dp)
                                .fillMaxHeight(heightFraction)
                        )
                    }
                    // draws horizontal line to the item
                    if (item.depth != 0) {
                        val startPadding = ((depthInset * (item.depth - 1)) + (depthInset / 2)) + initialDepthOffset
                        DashedHorizontalLine(
                            modifier = Modifier.padding(start = startPadding)
                                .width(depthInset / 2)
                                .height(depthInset)
                        )
                    }
                }

                // draws the expand toggle if possible
                if (collapsable && item.children != null) {
                    ExpandToggle(
                        expanded = !collapsedItems.contains(item.key),
                        onExpanded = { expanded ->
                            if (expanded) collapsedItems.remove(item.key)
                            else collapsedItems.add(item.key)
                        },
                        interactionSource = interactionSource,
                        modifier = Modifier
                            .padding(start = (depthInset * item.depth) + 4.dp)
                            .size(12.dp)
                            .align(Alignment.CenterStart)
                    )
                }

                // draws the actual content plus depth inset
                Row {
                    Spacer(Modifier.width((depthInset * item.depth) + initialDepthOffset))
                    item.content()
                }
            }
        }
    }
}

@Composable
private fun ExpandToggle(
    expanded: Boolean,
    onExpanded: (Boolean) -> Unit,
    interactionSource: MutableInteractionSource,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Win9xTheme.colorScheme.buttonHighlight)
            .border(1.dp, Win9xTheme.colorScheme.buttonShadow)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onExpanded(!expanded) }
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (expanded) "-" else "+",
            style = Win9xTheme.typography.default.copy(fontSize = 11.sp),
            modifier = Modifier.offset(0.5.dp, (-0.5).dp)
        )
    }
}

private val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)

@Composable
private fun DashedVerticalLine(
    modifier: Modifier = Modifier,
) {
    val dashColor = Win9xTheme.colorScheme.buttonShadow
    Canvas(modifier) {
        drawLine(
            color = dashColor,
            start = Offset(size.width / 2, 0f),
            end = Offset(size.width / 2, size.height),
            pathEffect = dashPathEffect
        )
    }
}

@Composable
private fun DashedHorizontalLine(
    modifier: Modifier = Modifier,
) {
    val dashColor = Win9xTheme.colorScheme.buttonShadow
    Canvas(modifier) {
        drawLine(
            color = dashColor,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            pathEffect = dashPathEffect
        )
    }
}
