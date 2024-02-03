package nl.ncaj.theme.win9x.controls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

internal actual fun runBlockingIfPossible(block: suspend CoroutineScope.() -> Unit) =
    runBlocking(block = block)