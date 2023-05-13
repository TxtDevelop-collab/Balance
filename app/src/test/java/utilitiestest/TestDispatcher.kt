import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import utilities.DispatcherProvider
import javax.inject.Inject

class TestDispatcherProvider : DispatcherProvider() {
    override val Main: CoroutineDispatcher = TestCoroutineDispatcher()
    override val IO: CoroutineDispatcher = TestCoroutineDispatcher()
    override val Default: CoroutineDispatcher = TestCoroutineDispatcher()
}
