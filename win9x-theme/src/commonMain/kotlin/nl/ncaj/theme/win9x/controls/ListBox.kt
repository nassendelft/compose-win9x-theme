package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.FocusDashIndication.Companion.focusDashIndication
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.selectionBackground
import nl.ncaj.theme.win9x.sunkenBorder


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ListBox(
    state: ListBoxState,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
    content: @Composable ListBoxItemScope.(index: Int) -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.White)
            .sunkenBorder()
            .padding(Win9xTheme.borderWidthDp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(2.dp)
                .verticalScroll(scrollState)
                .focusProperties { enter = { state.focusRequesters[state.selectedIndex] } }
                .onKeyEvent {
                    if (it.type == KeyEventType.KeyUp) {
                        if (it.key == Key.DirectionUp) {
                            state.onNavigateUp()
                            true
                        } else if (it.key == Key.DirectionDown) {
                            state.onNavigateDown()
                            true
                        } else false
                    } else false
                },
            content = {
                for (i in 0 until state.itemCount) {
                    ListBoxItemScope(state, i, this).apply { content(i) }
                }
            }
        )
        VerticalScrollBar(scrollState = scrollState)
    }
}

@Composable
fun rememberListBoxState(
    itemCount: Int,
    defaultIndex: Int = 0,
    onItemSelected: (index: Int) -> Unit = {}
) = remember(itemCount) { ListBoxState(itemCount, defaultIndex, onItemSelected) }

class ListBoxState internal constructor(
    internal val itemCount: Int,
    selectedIndex: Int = 0,
    private val onItemSelected: (index: Int) -> Unit,
) {
    var selectedIndex by mutableStateOf(selectedIndex)
        private set

    internal val disabledItems = mutableListOf<Int>()
    internal val focusRequesters = (0 until itemCount).map { FocusRequester() }

    internal fun onNavigateDown() {
        val nextAvailableIndex = getNextAvailableIndex()
        if (nextAvailableIndex != -1) {
            selectItem(nextAvailableIndex)
            focusRequesters[nextAvailableIndex].requestFocus()
        }
    }

    internal fun onNavigateUp() {
        val previousAvailableIndex = getPreviousAvailableIndex()
        if (previousAvailableIndex != -1) {
            selectItem(previousAvailableIndex)
            focusRequesters[previousAvailableIndex].requestFocus()
        }
    }

    fun selectItem(index: Int) {
        selectedIndex = index
        onItemSelected(index)
    }

    internal fun getNextAvailableIndex(): Int {
        var index = selectedIndex
        while (index < itemCount - 1) {
            if (disabledItems.contains(++index)) continue
            return index
        }
        return -1
    }

    internal fun getPreviousAvailableIndex(): Int {
        var index = selectedIndex
        while (index > 0) {
            if (disabledItems.contains(--index)) continue
            return index
        }
        return -1
    }
}

class ListBoxItemScope internal constructor(
    private val state: ListBoxState,
    private val index: Int,
    columnScope: ColumnScope,
) : ColumnScope by columnScope {

    fun Modifier.listBoxItem(
        enabled: Boolean = true,
        interactionSource: MutableInteractionSource? = null,
    ) = composed {
        val mutableInteractionSource = interactionSource ?: remember { MutableInteractionSource() }

        DisposableEffect(enabled) {
            if (enabled) state.disabledItems.remove(index)
            else state.disabledItems.add(index)
            onDispose { state.disabledItems.remove(index) }
        }

        Modifier
            .onFocusChanged { if (it.isFocused) state.selectItem(index) }
            .focusRequester(state.focusRequesters[index])
            .clickable(
                enabled = enabled,
                onClick = { state.selectItem(index) },
                indication = null,
                interactionSource = mutableInteractionSource,
            )
            .focusDashIndication(mutableInteractionSource)
            .selectionBackground(state.selectedIndex == index)
    }
}
