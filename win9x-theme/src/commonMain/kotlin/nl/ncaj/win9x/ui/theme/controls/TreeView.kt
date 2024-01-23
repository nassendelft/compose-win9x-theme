package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.win9x.ui.theme.DashFocusIndication.Companion.DashFocusIndicationNoPadding
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.sunkenBorder

class TreeViewScope internal constructor(
    private val columnScope: ColumnScope,
    private val depth: Int = 0,
    private val collapsable: Boolean,
    private val showRelationship: Boolean,
) {
    @Composable
    fun Item(
        label: String,
        modifier: Modifier = Modifier,
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        enabled: Boolean = true,
        leadingIcon: @Composable (RowScope.() -> Unit)? = null,
        content: @Composable (TreeViewScope.() -> Unit)? = null
    ) {
        var expanded by remember { mutableStateOf(true) }
        val isLeaveNode = content == null

        columnScope.apply {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = modifier.height(IntrinsicSize.Max),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (collapsable) Spacer(Modifier.width(6.dp))
                    if (depth > 0 || (!isLeaveNode && collapsable)) {
                        DashedHorizontalLine(
                            modifier = Modifier
                                .width(11.dp)
                                .then(if (showRelationship) Modifier else Modifier.alpha(0f))
                        )
                    }

                    leadingIcon?.invoke(this)?.also { Spacer(Modifier.width(4.dp)) }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = label,
                            enabled = enabled,
                            modifier = Modifier
                                .focusable(interactionSource = interactionSource)
                                .indication(
                                    interactionSource = interactionSource,
                                    indication = DashFocusIndicationNoPadding
                                )
                        )
                    }
                }

                if (!isLeaveNode && collapsable) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(Win9xTheme.colorScheme.buttonHighlight)
                            .border(1.dp, Win9xTheme.colorScheme.buttonShadow)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { expanded = !expanded }
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
            }

            if (expanded && content != null) {
                Row(modifier = Modifier.height(IntrinsicSize.Max)) {
                    DashedVerticalLine(
                        modifier = Modifier
                            .padding(start = 22.dp, bottom = 7.5.dp)
                            .width(1.dp)
                            .fillMaxHeight()
                            .then(if (collapsable) Modifier.offset(6.dp) else Modifier)
                            .then(if (showRelationship) Modifier else Modifier.alpha(0f))
                    )
                    Column {
                        TreeViewScope(
                            columnScope = this,
                            depth = depth + 1,
                            collapsable = collapsable,
                            showRelationship = showRelationship
                        ).content()
                    }
                }
            }
        }
    }
}

@Composable
fun TreeView(
    modifier: Modifier = Modifier,
    collapsable: Boolean = true,
    showRelationship: Boolean = true,
    withBorder: Boolean = true,
    content: @Composable TreeViewScope.() -> Unit
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .width(IntrinsicSize.Max)
            .then(if (withBorder) Modifier.sunkenBorder() else Modifier)
            .padding(Win9xTheme.borderWidthDp + 4.dp),
        content = {
            TreeViewScope(
                columnScope = this,
                collapsable = collapsable,
                showRelationship = showRelationship
            ).content()
        }
    )
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
