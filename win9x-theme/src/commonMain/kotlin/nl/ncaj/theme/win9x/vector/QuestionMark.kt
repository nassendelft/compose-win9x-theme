package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.QuestionMark: ImageVector
    get() {
        if (_questionMark != null) {
            return _questionMark!!
        }
        _questionMark = win9xIcon(
            name = "QuestionMark",
            size = Size(6f, 9f),
        ) {
            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveToRelative(0f, 1f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, -2f)
                close()
                moveTo(1f, 0f)
                lineToRelative(4f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-4f, 0f)
                lineToRelative(0f, -1f)
                close()
                moveTo(4f, 1f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, -2f)
                close()
                moveTo(3f, 3f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, -1f)
                close()
                moveTo(2f, 4f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, -2f)
                close()
                moveTo(2f, 7f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, -2f)
                close()
            }
        }
        return _questionMark!!
    }

private var _questionMark: ImageVector? = null
    
