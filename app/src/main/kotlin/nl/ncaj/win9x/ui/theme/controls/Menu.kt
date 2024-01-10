package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.SelectionIndication
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.windowBorder

@Preview
@Composable
fun MenuPreview() {
    Menu {
        label("Command") {}
        divider()
        checkBox("Check Box", checked = true) {}
        checkBox("Check Box Checked", checked = false) {}
        checkBox("Check Box Disabled", checked = true, enabled = false) {}
        divider()
        optionButton("Option Button", checked = true) {}
        optionButton("Option Button Checked", checked = false) {}
        optionButton("Option Button Disabled", checked = true, enabled = false) {}
        divider()
        label("Unavailable Item", enabled = false) {}
        cascade("Cascade Item") {}
        cascade("Cascade Item Disabled", enabled = false) {}
    }
}

class MenuItem(
    internal val content: @Composable () -> Unit
)

private val dividerMenuItem = MenuItem {
    val topLineColor = Win98Theme.colorScheme.buttonShadow
    val bottomLineColor = Win98Theme.colorScheme.buttonHighlight

    Canvas(
        modifier = Modifier
            .height(Win98Theme.borderWidthDp)
            .fillMaxWidth()
    ) {
        drawLine(
            color = topLineColor,
            start = Offset(0f, size.height / 4),
            end = Offset(size.width, size.height / 4),
            strokeWidth = size.height / 2
        )
        drawLine(
            color = bottomLineColor,
            start = Offset(0f, size.height - (size.height / 4)),
            end = Offset(size.width, size.height - (size.height / 4)),
            strokeWidth = size.height / 2
        )
    }
}

class MenuScope {
    internal val items = mutableListOf<MenuItem>()

    fun item(content: @Composable () -> Unit) = items.add(MenuItem(content))

    fun label(
        label: String,
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        onClick: () -> Unit,
    ) {
        val item = @Composable {
            Row(
                modifier = Modifier
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = SelectionIndication,
                        onClick = onClick
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    leadingIcon?.invoke()
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label, enabled = enabled)
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier.size(15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    trailingIcon?.invoke()
                }
            }
        }
        items.add(MenuItem(item))
    }

    fun checkBox(
        label: String,
        enabled: Boolean = true,
        checked: Boolean = false,
        icon: @Composable (() -> Unit)? = null,
        onCheckChanged: (Boolean) -> Unit,
    ) {
        val checkIcon = if (checked) {
            icon ?: {
                Image(
                    painter = painterResource(id = R.drawable.ic_checkmark),
                    contentDescription = "checked",
                    colorFilter = ColorFilter.tint(if (enabled) Color.Black else Color(0xFF808080)),
                )
            }
        } else null
        label(label, enabled, checkIcon) { onCheckChanged(!checked) }
    }

    fun optionButton(
        label: String,
        enabled: Boolean = true,
        checked: Boolean = false,
        onCheckChanged: (Boolean) -> Unit,
    ) {
        val optionIcon = @Composable {
            if (checked) {
                Image(
                    painter = painterResource(id = R.drawable.ic_option_button),
                    contentDescription = "checked",
                    colorFilter = ColorFilter.tint(
                        if (enabled) Color.Black else Color(0xFF808080)
                    )
                )
            }
        }
        label(label, enabled, optionIcon) { onCheckChanged(!checked) }
    }

    fun cascade(
        label: String,
        enabled: Boolean = true,
        onClick: () -> Unit,
    ) {
        val imageId = if (enabled) R.drawable.ic_arrow_down else R.drawable.ic_arrow_down_disabled
        val arrowIcon = @Composable {
            Image(
                painter = painterResource(id = imageId),
                contentDescription = "checked",
                modifier = Modifier.rotate(-90f),
            )
        }
        label(label, enabled = enabled, trailingIcon = arrowIcon, onClick = onClick)
    }

    fun divider() = items.add(dividerMenuItem)
}


@Composable
fun Menu(
    modifier: Modifier = Modifier,
    content: MenuScope.() -> Unit
) {
    val scope = MenuScope().apply(content)
    Column(
        modifier = modifier
            .width(IntrinsicSize.Max)
            .background(Win98Theme.colorScheme.buttonFace)
            .windowBorder()
            .padding(Win98Theme.borderWidthDp)
    ) {
        scope.items.forEach { item ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 1.dp, vertical = 3.dp),
            ) {
                item.content()
            }
        }
    }
}