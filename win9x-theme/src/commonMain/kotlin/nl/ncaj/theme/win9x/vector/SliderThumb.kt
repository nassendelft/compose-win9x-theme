package nl.ncaj.theme.win9x.vector

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

internal val Icons.SliderThumb: ImageVector
    get() {
        if (_sliderThumb != null) {
            return _sliderThumb!!
        }
        _sliderThumb = win9xIcon(
            name = "SliderThumb",
            size = Size(11f, 21f),
        ) {
            win9xPath(color = Color(0xff, 0xff, 0xff, 0xFF)) {
                moveTo(0f, 0f)
                verticalLineToRelative(16f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(-1f)
                horizontalLineTo(3f)
                verticalLineToRelative(-2f)
                horizontalLineTo(1f)
                verticalLineTo(1f)
                horizontalLineToRelative(9f)
                verticalLineTo(0f)
                close()
            }

            win9xPath(color = Color(0xC0, 0xC7, 0xC8, 0xFF)) {
                moveTo(1f, 1f)
                verticalLineToRelative(15f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(1f)
                verticalLineTo(1f)
                close()
            }

            win9xPath(color = Color(0x87, 0x88, 0x8F, 0xFF)) {
                moveTo(9f, 1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(15f)
                horizontalLineTo(8f)
                verticalLineToRelative(2f)
                horizontalLineTo(6f)
                verticalLineToRelative(2f)
                horizontalLineTo(5f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                close()
            }

            win9xPath(color = Color(0x00, 0x00, 0x00, 0xFF)) {
                moveTo(10f, 0f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(16f)
                horizontalLineTo(9f)
                verticalLineToRelative(2f)
                horizontalLineTo(7f)
                verticalLineToRelative(2f)
                horizontalLineTo(5f)
                verticalLineToRelative(1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                verticalLineToRelative(-2f)
                horizontalLineToRelative(2f)
                close()
            }
        }
        return _sliderThumb!!
    }

private var _sliderThumb: ImageVector? = null
    
