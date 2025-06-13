package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.selectionBackground
import nl.ncaj.theme.win9x.vector.ArrowDown
import nl.ncaj.theme.win9x.vector.Icons

@Composable
fun DropDownComboBox(
    value: String,
    onValueChange: (String) -> Unit,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    content: @Composable ColumnScope.() -> Unit
) = with(LocalDensity.current) {
    var containerSize = remember { IntSize.Zero }

    Column {
        TextBox(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.onGloballyPositioned { containerSize = it.size },
            enabled = enabled,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            visualTransformation = visualTransformation,
            onTextLayout = onTextLayout,
            interactionSource = interactionSource,
            cursorBrush = cursorBrush,
            trailingCommandButton = {
                Button(
                    onClick = { onExpandChange(!expanded) },
                    modifier = Modifier.width(14.dp),
                    borders = innerButtonBorders()
                ) {
                    Image(
                        painter = rememberVectorPainter(Icons.ArrowDown),
                        contentDescription = "",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        )
        if (expanded) {
            Box {
                Popup(
                    onDismissRequest = { onExpandChange(!expanded) },
                    properties = PopupProperties(focusable = true)
                ) {
                    Column(
                        modifier = Modifier
                            .width(containerSize.width.toDp())
                            .background(Win9xTheme.colorScheme.buttonHighlight)
                            .border(BorderStroke(1.dp, Win9xTheme.colorScheme.windowFrame)),
                        content = content
                    )
                }
            }
        }
    }
}