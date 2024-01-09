package nl.ncaj.win9x.ui.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.DashFocusIndication.Companion.DashFocusIndicationNoPadding
import nl.ncaj.win9x.ui.theme.controls.TypographyTokens

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
    val default: TextStyle = TypographyTokens.defaultTextStyle,
    val disabled: TextStyle = TypographyTokens.disabledTextStyle,
    val caption: TextStyle = TypographyTokens.captionTextStyle,
)

@Composable
fun Win98Theme(
    colorScheme: ColorScheme = Win98Theme.colorScheme,
    typography: Typography = Win98Theme.typography,
    content: @Composable () -> Unit
) {
    val rememberedColorScheme = remember {
        // Explicitly creating a new object here so we don't mutate the initial [colorScheme]
        // provided, and overwrite the values set in it.
        colorScheme.copy()
    }.apply { updateColorSchemeFrom(colorScheme) }

    CompositionLocalProvider(
        LocalColorScheme provides rememberedColorScheme,
        LocalTypography provides typography,
        LocalIndication provides DashFocusIndicationNoPadding,
        content = content
    )
}

internal val LocalColorScheme = staticCompositionLocalOf { ColorScheme() }
internal val LocalTypography = staticCompositionLocalOf { Typography() }

object Win98Theme {
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalColorScheme.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val borderWidthDp: Dp = 1.5.dp

    val borderWidthPx: Float
        @Composable
        @ReadOnlyComposable
        get() = with(LocalDensity.current) { borderWidthDp.toPx() }
}
