package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.rememberVectorResourcePainter

@Composable
fun SpinBox(
    value: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
) {
    TextBox(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        trailingCommandButton = {
            Column {
                Button(
                    onClick = onIncrease,
                    modifier = Modifier.size(15.dp, 12.dp),
                    borders = innerButtonBorders(),
                ) {
                    Image(
                        painter = rememberVectorResourcePainter("vector_images/ic_arrow_down.xml"),
                        contentDescription = "",
                        modifier = Modifier
                            .rotate(180f)
                            .wrapContentSize()
                    )
                }
                Button(
                    onClick = onDecrease,
                    modifier = Modifier.size(15.dp, 12.dp),
                    borders = innerButtonBorders(),
                ) {
                    Image(
                        painter = rememberVectorResourcePainter("vector_images/ic_arrow_down.xml"),
                        contentDescription = "",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    )
}