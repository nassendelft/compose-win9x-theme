package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.LayoutIdParentData
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.DashFocusIndication
import nl.ncaj.theme.win9x.Win9xTheme


internal class TreeViewItem(
    val children: (TreeViewScope.() -> Unit)? = null,
    val content: @Composable () -> Unit
)

class TreeViewScope internal constructor(
    internal val depth: Int = 0,
    internal val lineIndices: List<Int> = emptyList(),
) {
    internal val items = mutableListOf<TreeViewItem>()

    fun item(
        children: (TreeViewScope.() -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        items.add(TreeViewItem(children, content))
    }

    internal fun newTreeViewScope(item: TreeViewItem) = item.children?.let { children ->
        val isLastItem = items.indexOf(item) == items.size - 1
        val indices = if (isLastItem) lineIndices.filterNot { depth - 1 == it } else lineIndices

        TreeViewScope(
            depth = depth + 1,
            lineIndices = indices + depth
        ).apply(children)
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
//    val horizontalScroll = rememberScrollState()
//    val verticalScroll = rememberScrollState()
//    val scrollbarState = rememberScrollbarState(horizontalScroll, verticalScroll)
//    ScrollableHost(
//        scrollbarState = scrollbarState,
//        modifier = modifier.sunkenBorder()
//            .padding(Win9xTheme.borderWidthDp)
//    ) {
    TreeViewContent(
        content = content,
        collapsable = collapsable,
        showRelationship = showRelationship,
        modifier = modifier
            .background(Win9xTheme.colorScheme.buttonHighlight)
//            .horizontalScroll(horizontalScroll)
//            .verticalScroll(verticalScroll)
    )
//    }
}

@Composable
private fun TreeViewContent(
    modifier: Modifier = Modifier,
    collapsable: Boolean = true,
    showRelationship: Boolean = true,
    content: TreeViewScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val depthInset = 20.dp

    fun LazyListScope.render(content: TreeViewScope) {
        // If there's no depth (the root children have no children of its own) then there's need to draw
        // the expand toggle and thus no need to reserve any spacing at all
        val initialDepthOffset =
            if (!collapsable || (content.depth == 0 && content.items.all { it.children == null })) 0.dp
            else depthInset

        content.items.forEachIndexed { itemIndex, item ->
            val hasNextSibling = itemIndex < content.items.size - 1
            var expanded by mutableStateOf(true)

            item {
                Box(Modifier.height(IntrinsicSize.Min)) {
                    if (showRelationship) {
                        // draws vertical lines for each parent (to the root) that still has siblings
                        content.lineIndices.forEachIndexed { index, lineIndex ->
                            val shouldCutOff = !hasNextSibling && index == content.lineIndices.size - 1
                            DashedVerticalLine(
                                modifier = Modifier.padding(start = (depthInset * lineIndex) + initialDepthOffset)
                                    .width(20.dp)
                                    .fillMaxHeight()
                                    .then(
                                        // this should probably be half the size of max height instead of relying on
                                        // depthInset as it causes the layout to increase if the content is smaller
                                        if (shouldCutOff) Modifier.padding(bottom = depthInset / 2)
                                        else Modifier
                                    )
                            )
                        }
                        // draws horizontal line to the item
                        if (content.depth != 0) {
                            DashedHorizontalLine(
                                modifier = Modifier.padding(start = ((depthInset * (content.depth - 1)) + (depthInset / 2)) + initialDepthOffset)
                                    .width(depthInset / 2)
                                    .height(depthInset)
                            )
                        }
                    }

                    if (collapsable && item.children != null) {
                        ExpandToggle(
                            expanded = expanded,
                            onExpanded = { expanded = it },
                            interactionSource = interactionSource,
                            modifier = Modifier
                                .padding(start = (depthInset * content.depth) + 4.dp)
                                .size(12.dp)
                                .align(Alignment.CenterStart)
                        )
                    }

                    // finally only draw the actual content plus depth inset
                    Row {
                        Spacer(Modifier.width((depthInset * content.depth) + initialDepthOffset))
                        item.content()
                    }
                }
            }
            if (expanded) content.newTreeViewScope(item)?.let(::render)
        }
    }

    LazyColumn(
        modifier = modifier,
    ) {
        render(TreeViewScope().apply(content))
    }

//    Layout(
//        modifier = modifier,
//        content = {
//            val items = TreeViewScope().apply { content() }
//                .items
//                .reversed()
//                .map { 0 to it }
//                .toMutableList()
//            var absoluteIndex = 0
//            while (items.isNotEmpty()) {
//                val (depth, curr) = items.removeLast()
//                var expanded by remember { mutableStateOf(true) }
//
//                // root elements with no children don't require any start padding
//                if (depth == 0) {
//                    Box(Modifier.treeViewItem(absoluteIndex, depth, curr.hasChildren), content = { curr.content() })
//                } else if (curr.children == null || !collapsable) {
//                    Row(
//                        modifier = Modifier.treeViewItem(absoluteIndex, depth, curr.hasChildren),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        if (showRelationship) {
//                            Spacer(Modifier.width(10.dp))
//                            DashedHorizontalLine(Modifier.size(10.dp, 12.dp))
//                        } else {
//                            Spacer(Modifier.width(20.dp))
//                        }
//                        curr.content()
//                    }
//                } else {
//                    Row(
//                        modifier = Modifier.treeViewItem(absoluteIndex, depth, curr.hasChildren),
//                        verticalAlignment = Alignment.CenterVertically,
//                    ) {
//                        Spacer(Modifier.width(4.dp))
//                        ExpandToggle(
//                            expanded = expanded,
//                            onExpanded = { expanded = it },
//                            interactionSource = interactionSource,
//                            modifier = Modifier.size(12.dp)
//                        )
//                        if (showRelationship) {
//                            DashedHorizontalLine(Modifier.size(4.dp, 12.dp))
//                        } else {
//                            Spacer(Modifier.width(4.dp))
//                        }
//                        curr.content()
//                    }
//                }
//
//                if (expanded && curr.children != null) {
//                    if (showRelationship) {
//                        DashedVerticalLine(
//                            modifier = Modifier
//                                .width(20.dp)
//                                .dashLine(absoluteIndex + 1, depth + 1)
//                        )
//                    }
//
//                    curr.children
//                        .let { content -> TreeViewScope().apply { content() } }
//                        .items
//                        .reversed()
//                        .forEach { items.add(depth + 1 to it) }
//                }
//
//                absoluteIndex++
//            }
//        },
//        measurePolicy = { measurables, constraints ->
//            // measure all tree view items with no constraints
//            val treeViewItemPlacables = measurables.filter { it.layoutId != DashLineId }
//                .map { it.measure(Constraints()) to it }
//
//            // measure all vertical lines
//            val placeableVerticalLines = measurables.filter { it.layoutId == DashLineId }
//                .mapNotNull { lineMeasurable ->
//                    // from the same treeview item on the same index as the line to the last treeview item on the same depth
//                    val lineHeight = treeViewItemPlacables.drop(lineMeasurable.absoluteIndex)
//                        .takeWhile { it.second.depth >= lineMeasurable.depth }
//                        .takeIf { it.size > 1 }
//                        ?.let { list -> list.sumOf { it.first.height } - list.last().first.height / 2 }
//                        ?: return@mapNotNull null
//
////                    val depthPlaceables = treeViewItemPlacables.drop(lineMeasurable.absoluteIndex)
////                        .let { list ->
////                            var lastItem = 0
////                            for ((index, item) in list.withIndex()) {
////                                if (item.second.depth == lineMeasurable.depth) lastItem = index
////                                if (item.second.depth < lineMeasurable.depth) break
////                            }
////                            list.take(lastItem + 1)
////                        }
////                        .filter { it.first.height != Constraints.Infinity }
////                    val lineHeight = depthPlaceables.sumOf { (placeable) -> placeable.height }
//
////                    val offset =
////                        if (lineHeight == 0) 0 else depthPlaceables.last().first.height / 2
//
////                    val lineConstraint = Constraints.fixedHeight(lineHeight - offset)
//                    val lineConstraint = Constraints.fixedHeight(lineHeight)
//                    lineMeasurable.measure(lineConstraint) to lineMeasurable
//                }
//
//            val width =
//                constraints.constrainWidth(treeViewItemPlacables.maxOf { (placeable) -> placeable.width })
//            val height =
//                constraints.constrainHeight(treeViewItemPlacables.sumOf { (placeable) -> placeable.height })
//            layout(width, height) {
//                var itemY = 0
//                for ((placeable, data) in treeViewItemPlacables) {
//                    placeable.place(max(0, data.depth - 1) * depthInset.roundToPx(), itemY)
//                    itemY = min(height - placeable.height, itemY + placeable.height)
//                }
//
//                for ((placeable, measurable) in placeableVerticalLines) {
//                    val y = treeViewItemPlacables.subList(0, measurable.absoluteIndex)
//                        .sumOf { (placeable) -> placeable.height }
//                    placeable.place((measurable.depth - 1) * verticalLineInset.roundToPx(), y)
//                }
//            }
//        }
//    )
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

private fun Modifier.dashLine(index: Int, depth: Int) = this.then(
    TreeViewChildDataElement(
        depth = depth,
        layoutId = DashLineId,
        index = index,
        hasChildren = false,
        inspectorInfo = debugInspectorInfo {
            name = "dashLine"
            properties["index"] = index
            properties["depth"] = depth
        }
    )
)

private fun Modifier.treeViewItem(
    index: Int,
    depth: Int,
    hasChildren: Boolean
) = this.then(
    TreeViewChildDataElement(
        depth = depth,
        layoutId = TreeViewItemId,
        index = index,
        hasChildren = hasChildren,
        inspectorInfo = debugInspectorInfo {
            name = "treeViewItem"
            properties["index"] = index
            properties["depth"] = depth
            properties["children"] = hasChildren
        }
    )
)

private val Measurable.treeViewChildDataNode: TreeViewChildDataNode? get() = parentData as? TreeViewChildDataNode
private val Measurable.depth: Int get() = treeViewChildDataNode?.depth ?: 0
private val Measurable.absoluteIndex: Int get() = treeViewChildDataNode?.absoluteIndex ?: -1
private val Measurable.hasChildren: Boolean get() = treeViewChildDataNode?.hasChildren ?: false

private object DashLineId
private object TreeViewItemId

private class TreeViewChildDataElement(
    val depth: Int,
    val layoutId: Any,
    val index: Int,
    val hasChildren: Boolean,
    val inspectorInfo: InspectorInfo.() -> Unit
) : ModifierNodeElement<TreeViewChildDataNode>() {

    override fun create() = TreeViewChildDataNode(depth, layoutId, index, hasChildren)

    override fun update(node: TreeViewChildDataNode) {
        node.depth = depth
        node.layoutId = layoutId
        node.absoluteIndex = index
        node.hasChildren = hasChildren
    }

    override fun InspectorInfo.inspectableProperties() = inspectorInfo()

    override fun hashCode(): Int {
        var result = depth
        result = 31 * result + layoutId.hashCode()
        result = 31 * result + index
        result = 31 * result + hasChildren.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? TreeViewChildDataElement ?: return false
        return depth == otherModifier.depth
                && layoutId == otherModifier.layoutId
                && index == otherModifier.index
                && hasChildren == otherModifier.hasChildren
    }
}

private class TreeViewChildDataNode(
    var depth: Int,
    override var layoutId: Any,
    var absoluteIndex: Int,
    var hasChildren: Boolean,
) : ParentDataModifierNode, LayoutIdParentData, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@TreeViewChildDataNode
}
