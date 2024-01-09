package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.R

@Preview
@Composable
fun SpinBoxPreview() {
    val decimalRegex = """[^0-9]""".toRegex()
    var value by remember { mutableIntStateOf(1) }

    Column {
        Text("- Spin box -")
        Spacer(modifier = Modifier.height(2.dp))

        SpinBox(
            value.toString(),
            onValueChange = {
                value = decimalRegex.replace(it, "").takeIf { it.isNotBlank() }?.toInt() ?: 0
            },
            onIncrease = { value++ },
            onDecrease = { value-- },
        )
    }
}

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
                    borders = { innerButtonBorders() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "",
                        modifier = Modifier
                            .rotate(180f)
                            .wrapContentSize()
                    )
                }
                Button(
                    onClick = onDecrease,
                    modifier = Modifier.size(15.dp, 12.dp),
                    borders = { innerButtonBorders() }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_arrow_down),
                        contentDescription = "",
                        modifier = Modifier.wrapContentSize()
                    )
                }
            }
        }
    )
}