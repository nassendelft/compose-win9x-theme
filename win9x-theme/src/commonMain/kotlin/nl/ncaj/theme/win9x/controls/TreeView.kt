package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutIdParentData
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ParentDataModifierNode
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.DashFocusIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder
import kotlin.math.max
import kotlin.math.min


internal class TreeViewItem(
    val children: @Composable (TreeViewScope.() -> Unit)? = null,
    val content: @Composable () -> Unit
)

class TreeViewScope internal constructor() {
    internal val items = mutableListOf<TreeViewItem>()

    fun item(
        children: @Composable (TreeViewScope.() -> Unit)? = null,
        content: @Composable () -> Unit,
    ) {
        items.add(TreeViewItem(children, content))
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
        modifier = modifier
            .then(
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
                    if(isFocused) Modifier.background(Win9xTheme.colorScheme.selection)
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
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()
    val scrollbarState = rememberScrollbarState(horizontalScroll, verticalScroll)
    ScrollableHost(
        scrollbarState = scrollbarState,
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        TreeViewContent(
            content = content,
            collapsable = collapsable,
            showRelationship = showRelationship,
            modifier = modifier
                .background(Win9xTheme.colorScheme.buttonHighlight)
                .horizontalScroll(horizontalScroll)
                .verticalScroll(verticalScroll)
        )
    }
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
    val verticalLineInset = 20.dp

    Layout(
        modifier = modifier,
        content = {
            val items = TreeViewScope().apply { content() }
                .items
                .reversed()
                .map { 0 to it }
                .toMutableList()
            var index = 0
            while (items.isNotEmpty()) {
                val (depth, curr) = items.removeLast()
                var expanded by remember { mutableStateOf(true) }


                // root elements with no children don't require any start padding
                if (depth == 0) {
                    Box(Modifier.treeViewItem(index, depth), content = { curr.content() })
                } else if (curr.children == null || !collapsable) {
                    Row(
                        modifier = Modifier.treeViewItem(index, depth),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (showRelationship) {
                            Spacer(Modifier.width(10.dp))
                            DashedHorizontalLine(Modifier.size(10.dp, 12.dp))
                        } else {
                            Spacer(Modifier.width(20.dp))
                        }
                        curr.content()
                    }
                } else {
                    Row(
                        modifier = Modifier.treeViewItem(index, depth),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Spacer(Modifier.width(4.dp))
                        ExpandToggle(
                            expanded = expanded,
                            onExpanded = { expanded = it },
                            interactionSource = interactionSource,
                            modifier = Modifier.size(12.dp)
                        )
                        if (showRelationship) {
                            DashedHorizontalLine(Modifier.size(4.dp, 12.dp))
                        } else {
                            Spacer(Modifier.width(4.dp))
                        }
                        curr.content()
                    }
                }

                if (expanded && curr.children != null) {
                    if (showRelationship) {
                        DashedVerticalLine(
                            modifier = Modifier
                                .width(20.dp)
                                .dashLine(index + 1, depth + 1)
                        )
                    }

                    curr.children
                        .let { content -> TreeViewScope().apply { content() } }
                        .items
                        .reversed()
                        .forEach { items.add(depth + 1 to it) }
                }

                index++
            }
        },
        measurePolicy = { measurables, constraints ->
            val placeableItems = measurables.filter { it.layoutId != DashLineId }
                .map { it.measure(Constraints()) to it }

            val placeableVerticalLines = measurables.filter { it.layoutId == DashLineId }
                .map { measurable ->
                    val depthPlaceables = placeableItems.drop(measurable.index)
                        .let { list ->
                            var lastItem = 0
                            for ((index, item) in list.withIndex()) {
                                if (item.second.depth == measurable.depth) lastItem = index
                                if (item.second.depth < measurable.depth) break
                            }
                            list.take(lastItem+1)
                        }
                        .filter { it.first.height != Constraints.Infinity }
                    val lineHeight = depthPlaceables.sumOf { (placeable) -> placeable.height }

                    val offset =
                        if (lineHeight == 0) 0 else depthPlaceables.last().first.height / 2
                    measurable to lineHeight - offset
                }
                .map { (measurable, height) ->
                    measurable.measure(Constraints.fixedHeight(height)) to measurable
                }

            val width =
                constraints.constrainWidth(placeableItems.maxOf { (placeable) -> placeable.width })
            val height =
                constraints.constrainHeight(placeableItems.sumOf { (placeable) -> placeable.height })
            layout(width, height) {
                var itemY = 0
                for ((placeable, data) in placeableItems) {
                    placeable.place(max(0, data.depth - 1) * depthInset.roundToPx(), itemY)
                    itemY = min(height - placeable.height, itemY + placeable.height)
                }

                for ((placeable, measurable) in placeableVerticalLines) {
                    val y = placeableItems.subList(0, measurable.index)
                        .sumOf { (placeable) -> placeable.height }
                    placeable.place((measurable.depth - 1) * verticalLineInset.roundToPx(), y)
                }
            }
        }
    )
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
        inspectorInfo = debugInspectorInfo {
            name = "dashLine"
            properties["index"] = index
            properties["depth"] = depth
        }
    )
)

private fun Modifier.treeViewItem(index: Int, depth: Int) = this.then(
    TreeViewChildDataElement(
        depth = depth,
        layoutId = TreeViewItemId,
        index = index,
        inspectorInfo = debugInspectorInfo {
            name = "treeViewItem"
            properties["index"] = index
            properties["depth"] = depth
        }
    )
)

private val Measurable.treeViewChildDataNode: TreeViewChildDataNode? get() = parentData as? TreeViewChildDataNode
private val Measurable.depth: Int get() = treeViewChildDataNode?.depth ?: 0
private val Measurable.index: Int get() = treeViewChildDataNode?.index ?: -1

private object DashLineId
private object TreeViewItemId

private class TreeViewChildDataElement(
    val depth: Int,
    val layoutId: Any,
    val index: Int,
    val inspectorInfo: InspectorInfo.() -> Unit

) : ModifierNodeElement<TreeViewChildDataNode>() {
    override fun create(): TreeViewChildDataNode {
        return TreeViewChildDataNode(depth, layoutId, index)
    }

    override fun update(node: TreeViewChildDataNode) {
        node.depth = depth
        node.layoutId = layoutId
        node.index = index
    }

    override fun InspectorInfo.inspectableProperties() = inspectorInfo()

    override fun hashCode(): Int {
        var result = depth
        result = 31 * result + layoutId.hashCode()
        result = 31 * result + index
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherModifier = other as? TreeViewChildDataElement ?: return false
        return depth == otherModifier.depth
                && layoutId == otherModifier.layoutId
                && index == otherModifier.index
    }
}

private class TreeViewChildDataNode(
    var depth: Int,
    override var layoutId: Any,
    var index: Int,
) : ParentDataModifierNode, LayoutIdParentData, Modifier.Node() {
    override fun Density.modifyParentData(parentData: Any?) = this@TreeViewChildDataNode
}
