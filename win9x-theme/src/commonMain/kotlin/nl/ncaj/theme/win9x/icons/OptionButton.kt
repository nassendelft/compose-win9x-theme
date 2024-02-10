package nl.ncaj.theme.win9x.icons

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

fun Icons.optionButton(
    backgroundColor: Color,
): ImageVector = win9xIcon(
    name = "OptionButton",
    size = Size(12f, 12f),
) {
    win9xPath(color = Color(0x80, 0x80, 0x80, 0xFF)) {
        moveTo(8f, 0f)
        lineToRelative(-4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -2f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -2f)
        lineToRelative(2f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(2f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, -1f)
        close()
    }

    win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
        moveTo(8f, 1f)
        lineToRelative(-4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(2f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, -1f)
        close()
    }

    win9xPath(color = Color(0xDF, 0xDF, 0xDF, 0xFF)) {
        moveTo(9f, 3f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -1f)
        close()
        moveToRelative(1f, 5f)
        lineToRelative(0f, -4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 4f)
        lineToRelative(-1f, 0f)
        close()
        moveToRelative(-2f, 2f)
        lineToRelative(0f, -1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(-2f, 0f)
        close()
        moveToRelative(-4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(4f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-4f, 0f)
        close()
        moveToRelative(0f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(2f, 0f)
        close()
    }

    win9xPath(color = Color(0xFF, 0xFF, 0xFF, 0xFF)) {
        moveTo(11f, 2f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 4f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 2f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-4f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-2f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(2f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(4f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(2f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -2f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -4f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -2f)
        close()
    }

    win9xPath(color = backgroundColor) {
        moveTo(4f, 2f)
        lineToRelative(4f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, 4f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, 1f)
        lineToRelative(-4f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(-1f, 0f)
        lineToRelative(0f, -4f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        lineToRelative(1f, 0f)
        lineToRelative(0f, -1f)
        close()
    }
}

val Icons.OptionButtonDot: ImageVector
    get() {
        if (_optionButtonDot != null) {
            return _optionButtonDot!!
        }
        _optionButtonDot = win9xIcon(
            name = "OptionButtonDot",
            size = Size(4f, 4f),
        ) {
            win9xPath(color = Color(0xFF, 0xFF, 0xFF, 0xFF)) {
                moveToRelative(3f, 0f)
                lineToRelative(-2f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, 2f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, 1f)
                lineToRelative(2f, 0f)
                lineToRelative(0f, -1f)
                lineToRelative(1f, 0f)
                lineToRelative(0f, -2f)
                lineToRelative(-1f, 0f)
                lineToRelative(0f, -1f)
                close()
            }
        }
        return _optionButtonDot!!
    }

private var _optionButtonDot: ImageVector? = null
