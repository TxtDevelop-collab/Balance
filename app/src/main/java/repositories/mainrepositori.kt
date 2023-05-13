package repositories


import entities.balance.Transactions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import service.ApiService
import utilities.BaseDataSource
import utilities.DispatcherProvider
import javax.inject.Inject
import utilities.Result


//Here we handle the loggic to get the data, the dispatchers are inyected , so we could
//test this functions in a better way.

class mainrepositori@Inject constructor(private val myapiservice:ApiService,
                                        private val dispatcherProvider: DispatcherProvider):RepositoryTasks,BaseDataSource() {
    override suspend fun getDataFromServer(): Flow<Result<List<Transactions>>> = flow {
        try {
                emit(Result.loading(null)) // Emite un estado de carga inicial
                    val response = withTimeout(15000) {
                        myapiservice.getBalance()
                    }
                    if (response.isSuccessful) {
                        emit(Result.success(response.body()!!))
                    } else {
                        val message ="HTTP/" +response.code().toString()+ " " + response.message().toString()
                        emit(Result.error(message))
                    }

        } catch (e: Exception) {
            emit(Result.error("Error from server: ${e.localizedMessage}"))
        }
    }.flowOn(dispatcherProvider.IO)

}
//We have created this interface for two reason, as we have we created the class, we have separete the functioanlity
//Also we could recreate a FakeRepositoryTask that will allow us to create a fake output from the functions
interface RepositoryTasks{
   suspend fun getDataFromServer(): Flow<Result<List<Transactions>>>
}

/*Here we follow the SOLID protocol, we work on abstractions , we have separeted the way we get the
* data from the server, putting all the task in a interface(Single Responsability)
*
* Also, the Dependency Inversion (DIP) principle is met by relying on an abstraction,
* RepositoryTasks, instead of a concrete implementation of ApiService and DispatcherProvider.
* different implementations of RepositoryTasks can be provided without modifying the class that uses it.*/