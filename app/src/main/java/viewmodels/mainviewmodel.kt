package viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import entities.balance.Transactions
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import utilities.*
import java.io.IOException
import java.lang.Thread.State
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

/*
*  The viewModel checks the state of the network at the beginning, saving it in a stateFlow variable,
*  the getMyBalance function allows us to obtain the data from the server, using the repository,
*  in case of having a TimeOut, we repeat the task up to 3 times. We show the data in a LiveData.
*  The mapCorrectDataOrder function does all the things that are required.*/

@HiltViewModel
class Mainviewmodel@Inject constructor(private val repository:repositories.mainrepositori,
                                       private val connectivity: connectivity):ViewModel() {

    private var mutableconnectivity:MutableStateFlow<NetworkStatus.Status> = MutableStateFlow(NetworkStatus.Status.CHECKING)
    val internetstatus:StateFlow<NetworkStatus.Status> = mutableconnectivity
    private var mutablebalancedata:MutableLiveData<Result<List<Transactions>>> = MutableLiveData()
    val balancedata:LiveData<Result<List<Transactions>>> = mutablebalancedata
    init {
        viewModelScope.launch {
            connectivity.chheckConnectivity.collect{
                mutableconnectivity.value = it
            }
        }
    }
    fun getMyBalance() {
       viewModelScope.launch {
        if(mutableconnectivity.value==NetworkStatus.Status.COONECTED){
            Log.e("THREAD",Thread.currentThread().name)
            repository.getDataFromServer().retryWhen { cause, attempt ->
                if (cause is TimeoutCancellationException && attempt < 3) {
                    delay(5000)
                    true
                } else if (cause is IOException) {
                    delay(5000)
                    true
                }else{
                    false
                }
            }.catch {
                mutablebalancedata.value = Result.error("Failed")
            }.collect{
                if(it.status==Result.Status.ERROR){
                    mutablebalancedata.value = Result.error(it.message!!)
                }else if(it.status==utilities.Result.Status.SUCCESS && it.data!=null){
                    mutablebalancedata.value = Result.success(mapCorrectDataOrder(it.data))
                }
            }
        }else{
            mutablebalancedata.value = Result.error("NO INTERNET")
            }
        }
    }
    //With this function, we can handle all the requirements that are ordered.

    //1º- First we order the list by date
    //2º- Seconds as we have ordered the list, most recent transaction will be on top
    //3º- Third we find id from our orderList, and check the distinc, given us the final list.

    fun mapCorrectDataOrder(lst:List<Transactions>):List<Transactions>{
        val listadoOutput = mutableListOf<Transactions>()
        var orderList = mutableListOf<Transactions>()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val formatOK = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        try{
            lst.forEach {
                if (it.date!!.isValidDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")){
                    it.date  = formatOK.format(format.parse(it.date)).toString()
                    listadoOutput.add(it)
                }
            }
            orderList = listadoOutput.sortedByDescending { formatOK.parse(it.date)}.toMutableList()
            orderList = orderList.distinctBy { transactions ->
                transactions.id
            }.toMutableList()
            Log.e("","")
        }catch (ex:Exception){
            Log.e("ERROR - PARSE DATE",ex.message.toString())
        }
        return orderList
    }

}