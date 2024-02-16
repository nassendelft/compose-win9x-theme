package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.Maximize: ImageVector
    get() {
        if (_maximize != null) {
            return _maximize!!
        }
        _maximize = win9xIcon(
            name = "Maximize",
            size = Size(9f, 9f),
        ) {
            win9xPath {
                moveTo(0f, 0f)
                horizontalLineTo(9f)
                verticalLineToRelative(1f)
                horizontalLineTo(0f)
                close()
                moveTo(0f, 1f)
                verticalLineTo(8f)
                horizontalLineToRelative(1f)
                verticalLineTo(1f)
                close()
                moveTo(8f, 1f)
                verticalLineTo(8f)
                horizontalLineToRelative(1f)
                verticalLineTo(1f)
                close()
                moveTo(0f, 8f)
                horizontalLineTo(9f)
                verticalLineToRelative(12f)
                horizontalLineTo(0f)
                close()
                moveTo(1f, 1f)
                horizontalLineTo(8f)
                verticalLineToRelative(1f)
                horizontalLineTo(1f)
                close()
            }
        }
        return _maximize!!
    }

private var _maximize: ImageVector? = null
    
