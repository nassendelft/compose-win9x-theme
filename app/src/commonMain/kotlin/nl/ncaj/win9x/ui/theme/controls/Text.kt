package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import nl.ncaj.win9x.R
import nl.ncaj.win9x.ui.theme.Win9xTheme

internal object TypographyTokens {
    private val msSansSerifFamily = FontFamily(
        Font(R.font.ms_sans_serif_regular, FontWeight.Normal),
        Font(R.font.ms_sans_serif_bold, FontWeight.Bold)
    )

    val defaultTextStyle = TextStyle(
        color = Color(0xFF1A1A1A),
        fontFamily = msSansSerifFamily
    )

    val disabledTextStyle = defaultTextStyle.copy(
        color = Color(0xFF6D6D6D),
        shadow = Shadow(color = Color.White, offset = Offset(1f, 1f)),
    )

    val captionTextStyle = defaultTextStyle.copy(
        color = Color(0xFFFFFFFF),
    )
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: TextStyle = if (enabled) Win9xTheme.typography.default else Win9xTheme.typography.disabled,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: ColorProducer? = null
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style,
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        color = color
    )
}
