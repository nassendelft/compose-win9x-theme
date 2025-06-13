package nl.ncaj.theme.win9x

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun Win9xOverlayColorFilter(interactionSource: InteractionSource): ColorFilter? {
    val isFocused by interactionSource.collectIsFocusedAsState()
    return if (!isFocused) null
    else ColorFilter.tint(
        color = Win9xTheme.colorScheme.selection.copy(alpha = 0.5f),
        blendMode = BlendMode.SrcAtop
    )
}