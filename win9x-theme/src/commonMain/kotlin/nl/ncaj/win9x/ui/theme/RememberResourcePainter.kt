package nl.ncaj.win9x.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.LoadState
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.rememberImageVector
import org.jetbrains.compose.resources.resource

private object EmptyPainter : Painter() {

    // use size of 1 because when trying to draw size of zero it returns an error
    override val intrinsicSize = Size(1f, 1f)
    override fun DrawScope.onDraw() = Unit
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun rememberVectorResourcePainter(location: String): Painter {
    val state = resource(location).rememberImageVector(LocalDensity.current)
    return if (state is LoadState.Success<ImageVector>) rememberVectorPainter(state.value)
    else EmptyPainter // TODO: handle failure state
}

@OptIn(ExperimentalResourceApi::class)
@Composable
internal fun rememberBitmapResourcePainter(location: String): Painter {
    val state = resource(location).rememberImageBitmap()
    return if (state is LoadState.Success<ImageBitmap>) BitmapPainter(state.value)
    else EmptyPainter // TODO: handle failure state
}
