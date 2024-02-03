package nl.ncaj.theme.win9x.controls

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private val mainScope = MainScope()

/**
 * In a browser environment it's NOT blocking!
 * We use Dispatchers.Unconfined,
 * so if no suspension occurs, the block will complete before the completion of this function.
 */
internal actual fun runBlockingIfPossible(block: suspend CoroutineScope.() -> Unit) {
    mainScope.launch(context = Dispatchers.Unconfined, block = block)
}