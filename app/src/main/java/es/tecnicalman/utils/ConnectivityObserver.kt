package es.tecnicalman.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import es.tecnicalman.utils.room.NetworkUtils

@Composable
fun rememberIsConnected(): Boolean {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(NetworkUtils.isNetworkAvailable(context)) }

    DisposableEffect(context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnected = true
            }
            override fun onLost(network: Network) {
                isConnected = false
            }
        }
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        cm.registerNetworkCallback(request, networkCallback)
        onDispose {
            cm.unregisterNetworkCallback(networkCallback)
        }
    }
    return isConnected
}