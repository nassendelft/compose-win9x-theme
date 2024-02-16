package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.Minimize: ImageVector
    get() {
        if (_minimize != null) {
            return _minimize!!
        }
        _minimize = win9xIcon(
            name = "Minimize",
            size = Size(8f, 8f),
        ) {
            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveToRelative(1f, 6f)
                lineToRelative(6f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-6f, 0f)
                lineToRelative(0f, -2f)
                close()
            }
        }
        return _minimize!!
    }

private var _minimize: ImageVector? = null
    
