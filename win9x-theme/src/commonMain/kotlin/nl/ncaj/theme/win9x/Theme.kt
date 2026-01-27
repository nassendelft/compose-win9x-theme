package nl.ncaj.theme.win9x

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.ncaj.theme.win9x.controls.TypographyTokens

@Stable
class ColorScheme(
    windowFrame: Color = Color(0xFF0A0A0A),
    buttonFace: Color = Color(0xFFB3B3B3),
    buttonHighlight: Color = Color(0xFFFFFFFF),
    buttonShadow: Color = Color(0xFF808080),
    selection: Color = Color(0, 0, 123),
    activeCaption: Color = Color(0, 0, 123),
    captionText: Color = Color(0xFFFFFFFF),
) {
    var windowFrame by mutableStateOf(windowFrame, structuralEqualityPolicy())
        internal set
    var buttonFace by mutableStateOf(buttonFace, structuralEqualityPolicy())
        internal set
    var buttonHighlight by mutableStateOf(buttonHighlight, structuralEqualityPolicy())
        internal set
    var buttonShadow by mutableStateOf(buttonShadow, structuralEqualityPolicy())
        internal set
    var selection by mutableStateOf(selection, structuralEqualityPolicy())
        internal set
    var activeCaption by mutableStateOf(activeCaption, structuralEqualityPolicy())
        internal set
    var captionText by mutableStateOf(captionText, structuralEqualityPolicy())
        internal set

    fun copy(
        windowFrame: Color = this.windowFrame,
        buttonFace: Color = this.buttonFace,
        buttonHighlight: Color = this.buttonHighlight,
        buttonShadow: Color = this.buttonShadow,
        selection: Color = this.selection,
        activeCaption: Color = this.activeCaption,
        captionText: Color = this.captionText,
    ) = ColorScheme(
        windowFrame,
        buttonFace,
        buttonHighlight,
        buttonShadow,
        selection,
        activeCaption,
        captionText,
    )
}

internal fun ColorScheme.updateColorSchemeFrom(other: ColorScheme) {
    windowFrame = other.windowFrame
    buttonFace = other.buttonFace
    buttonHighlight = other.buttonHighlight
    buttonShadow = other.buttonShadow
    selection = other.selection
    activeCaption = other.activeCaption
    captionText = other.captionText
}

@Stable
class Typography(
    default: TextStyle = TextStyle(),
    disabled: TextStyle = TextStyle(),
    caption: TextStyle = TextStyle(),
    focused: TextStyle = TextStyle(),
) {
    var default by mutableStateOf(default, structuralEqualityPolicy())
    var disabled by mutableStateOf(disabled, structuralEqualityPolicy())
    var caption by mutableStateOf(caption, structuralEqualityPolicy())
    var focused by mutableStateOf(focused, structuralEqualityPolicy())

    fun copy(
        default: TextStyle = this.default,
        disabled: TextStyle = this.disabled,
        caption: TextStyle = this.caption,
    ) = Typography(default, disabled, caption)
}

internal fun Typography.updateTextStyleFrom(other: Typography) {
    default = other.default
    disabled = other.disabled
    caption = other.caption
    focused = other.focused
}

@Composable
fun Win9xTheme(
    colorScheme: ColorScheme = ColorScheme(),
    typography: Typography = defaultTypography,
    content: @Composable () -> Unit
) {
    val rememberedColorScheme = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colorScheme]
        // provided, and overwrite the values set in it.
        colorScheme.copy()
    }.apply { updateColorSchemeFrom(colorScheme) }

    val rememberedTypography = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colorScheme]
        // provided, and overwrite the values set in it.
        typography.copy()
    }.apply { updateTextStyleFrom(typography) }

    CompositionLocalProvider(
        LocalColorScheme provides rememberedColorScheme,
        LocalTypography provides rememberedTypography,
        LocalIndication provides FocusDashIndication(Dp.Unspecified),
        content = content
    )
}

internal val defaultTypography: Typography
    @Composable
    get() {
        val default = TypographyTokens.defaultTextStyle
        val disabled = TypographyTokens.disabledTextStyle
        val caption = TypographyTokens.captionTextStyle
        val focused = TypographyTokens.focusedTextStyle
        return Typography(default, disabled, caption, focused )
    }

internal val LocalColorScheme = staticCompositionLocalOf { ColorScheme() }
internal val LocalTypography = staticCompositionLocalOf { Typography() }

object Win9xTheme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: Typography
        @Composable
        get() = LocalTypography.current

    val borderWidthDp: Dp = 1.5.dp

    val borderWidthPx: Float
        @Composable
        @ReadOnlyComposable
        get() = with(LocalDensity.current) { borderWidthDp.toPx() }
}
