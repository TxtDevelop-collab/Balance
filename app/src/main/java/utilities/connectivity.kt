package utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject



/*Here we check for the connectivity, we are using callbackflow
* This allow us to check it in a better way than callbacks.
* The enum class allow us to show in a better way the status
*
*
* */
abstract class NetworkStatus {

    enum class Status {
        COONECTED, //Para la descarga de DAO
        NOT_CONNECTED,
        CHECKING
    }

}

class connectivity  @Inject constructor(
    private val connectivityManager: ConnectivityManager
){
        val chheckConnectivity: Flow<NetworkStatus.Status> = callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    trySend(NetworkStatus.Status.COONECTED)
                }

                override fun onLost(network: Network) {
                    trySend(NetworkStatus.Status.NOT_CONNECTED)
                    super.onLost(network)
                }
            }
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .apply {
                    addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()
            trySend(NetworkStatus.Status.CHECKING)
            connectivityManager.registerNetworkCallback(request, callback)
            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }
    val Context.isConnected: Boolean
        get() = (getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)?.activeNetworkInfo?.isConnected == true
}