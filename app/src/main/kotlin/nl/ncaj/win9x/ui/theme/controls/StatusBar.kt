package nl.ncaj.win9x.ui.theme.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import nl.ncaj.win9x.ui.theme.statusBarBorder

class StatusBarScope {
    internal val segments = mutableListOf<Segment>()

    fun segment(
        weight: Float? = null,
        content: @Composable () -> Unit
    ) = segments.add(Segment(weight, content))
}

internal class Segment(
    val weight: Float? = null,
    val content: @Composable () -> Unit,
)

@Composable
fun StatusBar(
    modifier: Modifier = Modifier,
    content: StatusBarScope.() -> Unit,
) {
    val scope = StatusBarScope().apply(content)
    Row(
        modifier
            .heightIn(min = 23.dp)
    ) {
        scope.segments.forEachIndexed { index, segment ->
            if (index != 0) Spacer(Modifier.width(1.dp))
            Box(
                modifier = Modifier
                    .then(segment.weight?.let { Modifier.weight(it) } ?: Modifier)
                    .statusBarBorder()
                    .padding(4.dp),
                content = { segment.content() }
            )
        }
    }
}