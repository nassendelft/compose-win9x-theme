package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.vector.ArrowDown
import nl.ncaj.theme.win9x.vector.Icons

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
        singleLine = true,
        trailingCommandButton = {
            Column {
                Button(
                    onClick = onIncrease,
                    modifier = Modifier.size(15.dp, 12.dp),
                    borders = innerButtonBorders(),
                ) {
                    Image(
                        painter = rememberVectorPainter(Icons.ArrowDown),
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
                        painter = rememberVectorPainter(Icons.ArrowDown),
                        contentDescription = "",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    )
}