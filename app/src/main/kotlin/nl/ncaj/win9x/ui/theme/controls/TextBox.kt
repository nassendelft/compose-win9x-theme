package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.Win98Theme
import nl.ncaj.win9x.ui.theme.sunkenBorder

@Composable
@Preview
fun TextBoxPreview() {
    var text by remember { mutableStateOf("Default") }
    Column {
        Text("- Text boxes - ")
        Spacer(modifier = Modifier.height(2.dp))
        TextBox(text, onValueChange = { text = it })
        Spacer(modifier = Modifier.height(2.dp))
        TextBox("Disabled", onValueChange = {}, enabled = false)
    }
}

@Composable
fun TextBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    trailingCommandButton: (@Composable () -> Unit)? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = if (enabled) Win98Theme.typography.default else Win98Theme.typography.disabled,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = cursorBrush,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .background(if (enabled) Win98Theme.colorScheme.buttonHighlight else Win98Theme.colorScheme.buttonFace)
                    .sunkenBorder()
                    .padding(Win98Theme.borderWidthDp)
            ) {
                Row {
                    Box(
                        Modifier
                            .defaultMinSize(minWidth = 100.dp)
                            .padding(4.dp)) {
                        innerTextField()
                    }
                    trailingCommandButton?.invoke()
                }
            }

        }
    )
}
