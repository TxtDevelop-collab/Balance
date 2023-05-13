package utilities

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import utilities.Result


/*This class has been created for future use,
*it will allow us to work with local data and apiservice data.
*Always having the most recent values.
*/
fun <T, A> performGetOperation(databaseQuery: () -> Flow<T>,
                               networkCall: suspend () -> Result<A>,
                               saveCallResult: suspend (A) -> Unit): Flow<Result<T>> =
    flow{
        emit(Result.loading(null))
        val source = databaseQuery.invoke().map { Result.success(it) }
        source.collect{
            emit(it)
        }
        val responseStatus = networkCall.invoke()
        if (responseStatus.status == Result.Status.SUCCESS) {
            saveCallResult(responseStatus.data!!)
        } else if (responseStatus.status == Result.Status.ERROR) {
            emit(Result.error<T>(responseStatus.message!!))
            source.collect{
                emit(it)
            }
        }
        //Volvemos ahora a cargar los datos con la base de datos.
    }.flowOn(Dispatchers.IO)

