package utilities

import retrofit2.Response
import utilities.Result

/*
* This class help us to handle the reponse from the server, we work with abstraccion
* Why we have created that class if we are not using it, if you check the repository,
* we have done the same as we are doing here, if our app increase on size, we need to
* delegate how our response is handled and pass just the result to the flow,
* so the logict showed in the getDataFromServer are gonna be replaced by getResult and
* this function will just give back Flow<Reslt<data>> */
abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Result<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val getCode = response.code()
                if(getCode!=200){
                    return error("${response.code()} ${response.message()}")
                }
                val body = response.body()
                if (body != null) return Result.success(body)
            }
            return error(" ${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Result<T> {

        return Result.error("Network call has failed for a following reason: $message")
    }



}