package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.Checkmark: ImageVector
    get() {
        if (_checkmark != null) {
            return _checkmark!!
        }
        _checkmark = win9xIcon(
            name = "Checkmark",
            size = Size(7f, 7f),
        ) {
            win9xPath(color = Color(0xFF, 0xFF, 0xFF, 0xFF)) {
                moveTo(7f, 0f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 3f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -3f)
                close()
            }
        }
        return _checkmark!!
    }

private var _checkmark: ImageVector? = null
    
