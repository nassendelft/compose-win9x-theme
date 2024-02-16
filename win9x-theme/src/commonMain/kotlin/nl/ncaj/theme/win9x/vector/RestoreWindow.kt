package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.RestoreWindow: ImageVector
    get() {
        if (_restoreWindow != null) {
            return _restoreWindow!!
        }
        _restoreWindow = win9xIcon(
            name = "RestoreWindow",
            size = Size(8f, 9f),
        ) {
            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveToRelative(2f, 0f)
                lineToRelative(6f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-6f, 0f)
                lineToRelative(0f, -2f)
                close()
                moveTo(7f, 2f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 4f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -4f)
                close()
                moveTo(2f, 2f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -1f)
                close()
                moveTo(6f, 5f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -1f)
                close()
                moveTo(0f, 3f)
                lineToRelative(6f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-6f, 0f)
                lineToRelative(0f, -2f)
                close()
                moveTo(5f, 5f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 4f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -4f)
                close()
                moveTo(0f, 5f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 4f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -4f)
                close()
                moveTo(1f, 8f)
                lineToRelative(4f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-4f, 0f)
                lineToRelative(0f, -1f)
                close()
            }
        }
        return _restoreWindow!!
    }

private var _restoreWindow: ImageVector? = null
    
