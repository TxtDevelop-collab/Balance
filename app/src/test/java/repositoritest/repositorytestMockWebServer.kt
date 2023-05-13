package repositoritest

import TestDispatcherProvider
import com.example.balance.MainCoroutineRule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import entities.balance.Transactions
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.*
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import repositories.mainrepositori
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import service.ApiService


class repositorytestMockWebServer {

    private lateinit var mockWebServer: MockWebServer

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Mock
    lateinit var myApiservice:ApiService
    @Mock
    lateinit var repository:mainrepositori
    private lateinit var retrofit: Retrofit

    private lateinit var dispatcherProvider: TestDispatcherProvider
    @Before
    fun setUpd(){
        mockWebServer = MockWebServer()
        mockWebServer.start()

        dispatcherProvider = TestDispatcherProvider()
        // Configura Retrofit con la URL base del servidor simulado
        retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        myApiservice = retrofit.create(ApiService::class.java)
        repository = mainrepositori(myApiservice,dispatcherProvider)
    }

    @Test
    fun getdatafromserverwithouterror()=   runBlocking {

        val list:List<Transactions> = listOf(
         Transactions(
             id = "12345",
            date = "2023-05-11",
            amount = 1000.00,
            fee = 10.00,
            description = "Payment for goods"
         ),
        Transactions(
            id = "12346",
            date = "2023-05-11",
            amount = 1000.00,
            fee = 10.00,
            description = "Payment for goods"
        )
        )
        val gson = GsonBuilder().create()

        val response = MockResponse()
            .setResponseCode(200)
            .setBody(gson.toJson(list))
        mockWebServer.enqueue(response)
        // Verifica que la respuesta es exitosa
        repository.getDataFromServer().collect{
            if(it.status==utilities.Result.Status.SUCCESS){
                val IdData=it.data!!.get(0).id
                assertEquals("12345",IdData)
                println("Response string: $IdData")
            }

        }

    }
    @Test
    fun getdatafromserverwitherror()=  runBlocking {
        val response = MockResponse()
            .setResponseCode(400)
            .setBody("Client Error")
        mockWebServer.enqueue(response)
        // Verifica que la respuesta es exitosa
        val result = repository.getDataFromServer()
        result.collect{
            if(it.status==utilities.Result.Status.ERROR){
                val recordedRequest = mockWebServer.takeRequest()
                assertEquals("GET", response.http2ErrorCode)
                assertEquals(it.message.toString(),"HTTP/400 Client Error")
            }
        }
    }
}