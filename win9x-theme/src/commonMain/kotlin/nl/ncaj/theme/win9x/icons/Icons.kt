package nl.ncaj.theme.win9x.icons

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object Icons

/**
 * Adds a vector path to this icon with defaults.
 *
 * @param fillAlpha fill alpha for this path
 * @param strokeAlpha stroke alpha for this path
 * @param pathFillType [PathFillType] for this path
 * @param pathBuilder builder lambda to add commands to this path
 */
inline fun ImageVector.Builder.win9xPath(
    fillAlpha: Float = 1f,
    strokeAlpha: Float = 1f,
    pathFillType: PathFillType = DefaultFillType,
    color: Color = Color.Black,
    pathBuilder: PathBuilder.() -> Unit,
) = path(
    fill = SolidColor(color),
    fillAlpha = fillAlpha,
    stroke = null,
    strokeAlpha = strokeAlpha,
    strokeLineWidth = 1f,
    strokeLineCap = StrokeCap.Butt,
    strokeLineJoin = StrokeJoin.Bevel,
    strokeLineMiter = 1f,
    pathFillType = pathFillType,
    pathBuilder = pathBuilder
)

/**
 * Utility delegate to construct an icon with default size information.
 * This is used by generated icons, and should not be used manually.
 *
 * @param name the full name of the generated icon
 * @param autoMirror determines if the vector asset should automatically be mirrored for right to
 * left locales
 * @param size the size of the icon
 * @param block builder lambda to add paths to this vector asset
 */
inline fun win9xIcon(
    name: String,
    autoMirror: Boolean = false,
    size: Size = Size(24f, 24f),
    block: ImageVector.Builder.() -> ImageVector.Builder,
): ImageVector = ImageVector.Builder(
    name = name,
    defaultWidth = size.width.dp,
    defaultHeight = size.height.dp,
    viewportWidth = size.width,
    viewportHeight = size.height,
    autoMirror = autoMirror
).block().build()
