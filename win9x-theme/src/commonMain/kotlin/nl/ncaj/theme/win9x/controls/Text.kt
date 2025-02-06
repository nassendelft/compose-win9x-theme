package nl.ncaj.theme.win9x.controls

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import nl.ncaj.theme.win9x.Win9xTheme
import nl.ncaj.theme.win9x.win9x_theme.generated.resources.Res
import nl.ncaj.theme.win9x.win9x_theme.generated.resources.ms_sans_serif_bold
import nl.ncaj.theme.win9x.win9x_theme.generated.resources.ms_sans_serif_regular
import org.jetbrains.compose.resources.Font

internal object TypographyTokens {
    private val msSansSerifFamily: FontFamily
        @Composable
        get() = FontFamily(
            Font(
                resource = Res.font.ms_sans_serif_regular,
                weight = FontWeight.Normal,
                style = FontStyle.Normal,
            ),
            Font(
                resource = Res.font.ms_sans_serif_bold,
                weight = FontWeight.Bold,
                style = FontStyle.Normal,
            )
        )

    val defaultTextStyle @Composable get() = TextStyle(
        color = Color(0xFF1A1A1A),
        fontFamily = msSansSerifFamily
    )

    val disabledTextStyle @Composable get() = defaultTextStyle.copy(
        color = Color(0xFF6D6D6D),
        shadow = Shadow(color = Color.White, offset = Offset(1f, 1f)),
    )

    val captionTextStyle @Composable get() = defaultTextStyle.copy(
        color = Color(0xFFFFFFFF),
    )

    val focusedTextStyle @Composable get() = defaultTextStyle.copy(
        color = Color(0xFFFFFFFF),
    )
}

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextDefaults.style(),
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

@Composable
fun Text(
    text: String,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    hoverable: Boolean = false,
    selected: Boolean = false,
    style: TextStyle = TextDefaults.style(interactionSource, enabled, hoverable, selected),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    color: ColorProducer? = null
) {
    Text(
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

object TextDefaults {
    @Composable
    fun style(
        interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
        enabled: Boolean = true,
        hoverable: Boolean = false,
        selected: Boolean = false,
    ): TextStyle {
        val hovered by interactionSource.collectIsHoveredAsState()
        val focused by interactionSource.collectIsFocusedAsState()
        return when {
            !enabled -> Win9xTheme.typography.disabled
            selected -> Win9xTheme.typography.focused
            hovered && hoverable -> Win9xTheme.typography.focused
            focused -> Win9xTheme.typography.focused
            else -> Win9xTheme.typography.default
        }
    }
}
