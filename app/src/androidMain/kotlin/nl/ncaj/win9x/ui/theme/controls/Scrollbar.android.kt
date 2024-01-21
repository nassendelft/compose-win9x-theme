package nl.ncaj.win9x.ui.theme.controls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

internal actual fun runBlockingIfPossible(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(block = block)