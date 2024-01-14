package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.Win9xTheme
import nl.ncaj.win9x.ui.theme.sunkenBorder
import nl.ncaj.win9x.ui.theme.windowBorder

@Composable
@Preview
fun WindowPreview() {
    val windowState = WindowState(
        onCloseRequested = {},
        sizingActions = WindowState.SizingActions(
            onMaximizeRequested = {},
            onRestoreSizeRequested = {},
        ),
        onMinimizeRequested = {}
    )
    Window(
        title = "Title",
        windowState = windowState,
        menuBar = {
            entry("Item1") {
                label("Sub menu item 1") {}
            }
            entry("Item2") {
                label("Sub menu item 1") {}
                cascade("Sub menu item 2") {
                    label("Cascade menu item 1") {}
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White)
                .sunkenBorder()
        )
    }
}

@Composable
fun TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    buttons: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .background(Win9xTheme.colorScheme.activeCaption)
            .height(18.dp)
            .defaultMinSize(minWidth = 100.dp)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.invoke()
        Text(
            text = title,
            style = Win9xTheme.typography.caption,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        Spacer(Modifier.weight(1f))
        buttons?.invoke()
    }
}

@Stable
class WindowState(
    internal val sizingActions: SizingActions? = null,
    internal val onHelp: (() -> Unit)? = null,
    internal val closeEnabled: Boolean = true,
    internal val onMinimizeRequested: (() -> Unit)? = null,
    internal val onCloseRequested: () -> Unit,
) {
    internal val sizingImage = mutableIntStateOf(R.drawable.ic_maximize)
    internal val sizingAction = mutableStateOf(sizingActions?.onMaximizeRequested)
    internal val showMinimized = mutableStateOf(onMinimizeRequested != null)

    fun setSizing(sizing: Sizing) {
        when (sizing) {
            Sizing.Maximized -> {
                sizingImage.intValue = R.drawable.ic_restore_window
                sizingAction.value = sizingActions?.onRestoreSizeRequested
                showMinimized.value = onMinimizeRequested != null
            }

            Sizing.Minimized -> {
                sizingImage.intValue = R.drawable.ic_maximize
                sizingAction.value = sizingActions?.onRestoreSizeRequested
                showMinimized.value = false
            }

            Sizing.Custom -> {
                sizingImage.intValue = R.drawable.ic_maximize
                sizingAction.value = sizingActions?.onMaximizeRequested
                showMinimized.value = onMinimizeRequested != null
            }
        }
    }

    enum class Sizing { Maximized, Minimized, Custom }

    class SizingActions(
        val onMaximizeRequested: () -> Unit,
        val onRestoreSizeRequested: () -> Unit,
    )
}

@Composable
fun Window(
    title: String,
    windowState: WindowState,
    modifier: Modifier = Modifier,
    menuBar: (MenuBarScope.() -> Unit)? = null,
    statusBar: (StatusBarScope.() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .background(Win9xTheme.colorScheme.buttonFace)
            .windowBorder()
            .padding(Win9xTheme.borderWidthDp + 2.dp)
            .defaultMinSize(minHeight = 100.dp)
    ) {
        TitleBar(title) {
            windowState.onMinimizeRequested?.let { onMinimizeRequested ->
                if (windowState.showMinimized.value) {
                    TitleButton(
                        resourceId = R.drawable.ic_minimize,
                        onClick = onMinimizeRequested
                    )
                }
            }
            if (windowState.sizingActions != null) {
                TitleButton(
                    resourceId = windowState.sizingImage.intValue,
                    onClick = { windowState.sizingAction.value?.invoke() }
                )
            }
            windowState.onHelp?.let {
                TitleButton(resourceId = R.drawable.ic_question_mark, onClick = it)
            }
            Spacer(Modifier.width(1.dp))
            TitleButton(
                resourceId = R.drawable.ic_cross,
                onClick = windowState.onCloseRequested,
                enabled = windowState.closeEnabled
            )
        }

        menuBar?.let { MenuBar(content = it) }

        Spacer(modifier = Modifier.height(2.dp))

        Box(Modifier.weight(1f)) {
            content()
        }

        statusBar?.let {
            Spacer(modifier = Modifier.height(2.dp))
            StatusBar(content = it)
        }
    }
}

@Composable
private fun TitleButton(
    resourceId: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(14.dp),
        enabled = enabled,
        defaultPadding = PaddingValues(),
        borders = innerButtonBorders(),
    ) {
        Image(
            painter = painterResource(id = resourceId),
            contentDescription = "Close window",
        )
    }
}