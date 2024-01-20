package nl.ncaj.win9x.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.LoadState
import org.jetbrains.compose.resources.rememberImageVector
import org.jetbrains.compose.resources.resource

private object EmptyPainter : Painter() {
    override val intrinsicSize = Size.Zero
    override fun DrawScope.onDraw() = Unit
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun rememberVectorResourcePainter(location: String): Painter {
    val state = resource(location).rememberImageVector(LocalDensity.current)
    return if (state is LoadState.Success<ImageVector>) rememberVectorPainter(state.value)
    else EmptyPainter // TODO: handle failure state
}
