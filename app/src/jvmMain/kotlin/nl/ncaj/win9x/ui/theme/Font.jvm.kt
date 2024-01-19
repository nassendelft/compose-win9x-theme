package nl.ncaj.win9x.ui.theme

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

private object Resource {
    fun readBytes(file: String) = javaClass.getResourceAsStream(file)?.readBytes() ?: ByteArray(0)
}

internal actual val msSansSerifFamilyNormal = Font(
    identity = "msSansSerifFamilyNormal",
    data = Resource.readBytes("/font/ms_sans_serif_regular.ttf"),
)

internal actual val msSansSerifFamilyBold = Font(
    identity = "msSansSerifFamilyBold",
    data = Resource.readBytes("/font/ms_sans_serif_bold.ttf"),
    weight = FontWeight.Bold
)
