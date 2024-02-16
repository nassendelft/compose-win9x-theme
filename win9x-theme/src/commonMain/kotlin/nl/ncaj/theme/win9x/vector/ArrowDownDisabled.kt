package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.ArrowDownDisabled: ImageVector
    get() {
        if (_arrowDownDisabled != null) {
            return _arrowDownDisabled!!
        }
        _arrowDownDisabled = win9xIcon(
            name = "ArrowDownDisabled",
            size = Size(8f, 5f),
        ) {
            win9xPath(color = Color(0xFF, 0xFF, 0xFF, 0xFF)) {
                moveToRelative(7f, 1f)
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
                close()
            }

            win9xPath(color = Color(0x80, 0x80, 0x80, 0xFF)) {
                moveToRelative(8f, 0f)
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
                close()
            }
        }
        return _arrowDownDisabled!!
    }

private var _arrowDownDisabled: ImageVector? = null
    
