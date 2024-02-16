package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.ArrowDown: ImageVector
    get() {
        if (_arrowDown != null) {
            return _arrowDown!!
        }
        _arrowDown = win9xIcon(
            name = "ArrowDown",
            size = Size(7f, 4f),
        ) {
            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveToRelative(7f, 0f)
                lineToRelative(-7f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
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
            }
        }
        return _arrowDown!!
    }

private var _arrowDown: ImageVector? = null
    
