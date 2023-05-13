package viewmodeltest

import TestDispatcherProvider
import android.net.ConnectivityManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.balance.MainCoroutineRule
import entities.balance.Transactions
import getOrAwaitValue
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import repositories.mainrepositori
import service.ApiService
import utilities.connectivity
import viewmodels.Mainviewmodel

@RunWith(MockitoJUnitRunner::class)
class Mainviewmodeltest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Creamos un mock de la interfaz ConnectivityManager
    private val mockConnectivityManager: ConnectivityManager = mock()

    private lateinit var myrepository:mainrepositori

    @Mock
    private lateinit var myApiService:ApiService

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dispatcherProvider: TestDispatcherProvider
    private lateinit var myviewModel:Mainviewmodel
    @Before
    fun setUp(){
        dispatcherProvider = TestDispatcherProvider()
        myApiService = mock(ApiService::class.java)
        myrepository = mainrepositori(myApiService,dispatcherProvider)
        myviewModel = Mainviewmodel(myrepository, connectivity(mockConnectivityManager))

    }
    @Test
    fun `getDataFromServer should return the expected data`() = mainCoroutineRule.runTest{

        val mockResponse = listOf(
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
        doReturn(flowOf(Result.success(mockResponse))).`when`(myApiService).getBalance()
        myviewModel.getMyBalance()
        val data =  myviewModel.balancedata.getOrAwaitValue()
        Assert.assertEquals(2, data.data!!.size)
    }


}