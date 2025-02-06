package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.windowBorder

@Composable
fun TitleBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .background(Win9xTheme.colorScheme.activeCaption)
            .height(18.dp)
            .defaultMinSize(minWidth = 100.dp)
            .padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
fun TitleBar(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    buttons: (@Composable () -> Unit)? = null,
) {
    TitleBar(modifier) {
        icon?.invoke()
        Text(
            text = title,
            style = Win9xTheme.typography.caption.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 2.dp)
        )
        Spacer(Modifier.weight(1f))
        buttons?.invoke()
    }
}

@Composable
fun Window(
    title: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    menuBar: (@Composable () -> Unit)? = null,
    statusBar: (StatusBarScope.() -> Unit)? = null,
    buttons: (@Composable () -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Window(
        modifier = modifier,
        menuBar = menuBar,
        statusBar = statusBar,
        content = content,
        titleBar = {
            TitleBar(
                title = title,
                icon = icon,
                buttons = buttons
            )
        }
    )
}

@Composable
fun Window(
    titleBar: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    menuBar: (@Composable () -> Unit)? = null,
    statusBar: (StatusBarScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(Win9xTheme.colorScheme.buttonFace)
            .windowBorder()
            .padding(Win9xTheme.borderWidthDp + 2.dp)
            .defaultMinSize(minHeight = 100.dp)
    ) {
        titleBar()

        menuBar?.let { bar ->
            Spacer(Modifier.height(1.dp))
            bar()
            Spacer(Modifier.height(1.dp))
        }

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
fun TitleButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier.size(14.dp)
            .focusProperties { canFocus = false },
        enabled = enabled,
        defaultPadding = PaddingValues(),
        interactionSource = interactionSource,
        borders = innerButtonBorders(),
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
        )
    }
}