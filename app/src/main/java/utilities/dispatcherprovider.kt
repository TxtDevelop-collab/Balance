package utilities

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

//Class that handle the dispacthers.
open class DispatcherProvider @Inject constructor() {
    open val Main: CoroutineDispatcher = Dispatchers.Main
    open val IO: CoroutineDispatcher = Dispatchers.IO
    open val Default: CoroutineDispatcher = Dispatchers.Default
}