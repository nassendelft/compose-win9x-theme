package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.Cross: ImageVector
    get() {
        if (_cross != null) {
            return _cross!!
        }
        _cross = win9xIcon(
            name = "Cross",
            size = Size(8f, 7f),
        ) {
            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveTo(0f, 0f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(2f)
                verticalLineTo(1f)
                horizontalLineToRelative(1f)
                verticalLineTo(0f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(1f)
                horizontalLineTo(7f)
                verticalLineToRelative(1f)
                horizontalLineTo(6f)
                verticalLineToRelative(1f)
                horizontalLineTo(5f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineTo(6f)
                verticalLineTo(6f)
                horizontalLineTo(5f)
                verticalLineTo(5f)
                horizontalLineTo(3f)
                verticalLineToRelative(1f)
                horizontalLineTo(2f)
                verticalLineToRelative(1f)
                horizontalLineTo(0f)
                verticalLineTo(6f)
                horizontalLineToRelative(1f)
                verticalLineTo(5f)
                horizontalLineToRelative(1f)
                verticalLineTo(4f)
                horizontalLineToRelative(1f)
                verticalLineTo(3f)
                horizontalLineTo(2f)
                verticalLineTo(2f)
                horizontalLineTo(1f)
                verticalLineTo(1f)
                horizontalLineTo(0f)
                verticalLineTo(0f)
                close()
            }
        }
        return _cross!!
    }

private var _cross: ImageVector? = null
    
