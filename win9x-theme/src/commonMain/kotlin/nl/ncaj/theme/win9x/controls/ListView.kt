package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.FocusDashIndication.Companion.FocusDashIndication
import nl.ncaj.theme.win9x.FocusSelectionIndication.Companion.focusSelectionIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.sunkenBorder


@Composable
fun LargeIconListItem(
    label: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onclick: () -> Unit = {},
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Column(
        modifier = modifier.size(80.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onclick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.sizeIn(maxWidth = 36.dp, maxHeight = 36.dp)) {
            icon()
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = textStyle.copy(fontSize = 12.sp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .focusSelectionIndication(interactionSource)
                .FocusDashIndication(interactionSource)
                .padding(horizontal = 1.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun SmallIconListItem(
    label: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    onclick: () -> Unit = {},
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    val textStyle = when {
        !enabled -> Win9xTheme.typography.disabled
        isFocused -> Win9xTheme.typography.caption
        else -> Win9xTheme.typography.default
    }

    Box(modifier = modifier.defaultMinSize(minWidth = 100.dp)) {
        Row(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onclick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.sizeIn(maxWidth = 18.dp, maxHeight = 18.dp)) {
                icon()
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                style = textStyle.copy(fontSize = 12.sp),
                modifier = Modifier
                    .focusSelectionIndication(interactionSource)
                    .FocusDashIndication(interactionSource)
                    .padding(horizontal = 1.dp, vertical = 2.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HorizontalListView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val horizontalScroll = rememberScrollState()
    ScrollableHost(
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScroll),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        FlowColumn(
            modifier = modifier
                .background(Color.White)
                .horizontalScroll(horizontalScroll),
            content = { content() }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerticalListView(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val verticalScroll = rememberScrollState()
    ScrollableHost(
        verticalScrollbarAdapter = rememberScrollbarAdapter(verticalScroll),
        modifier = modifier.sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        FlowRow(
            modifier = modifier
                .background(Color.White)
                .verticalScroll(verticalScroll),
            content = { content() },
        )
    }
}

