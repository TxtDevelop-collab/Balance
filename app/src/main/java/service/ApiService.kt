package service

import entities.balance.Transactions
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    //We put here the headers, but we could create a new Interceptor in the own Retrofit
    //and check there the reponse.
    @Headers("Content-Type:application/json; charset=UTF-8")
    @GET("/transactions.json")
    suspend fun getBalance(): Response<List<Transactions>>
}