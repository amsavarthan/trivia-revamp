package quiz.game.trivia.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Throws(NetworkException.NoConnectionException::class)
    inline fun <T> withInternetConnection(execute: () -> T): T {
        if (!isInternetAvailable()) throw NetworkException.NoConnectionException
        return execute()
    }

    fun isInternetAvailable(): Boolean {
        return postAndroidMInternetCheck(connectivityManager)
    }

    private fun postAndroidMInternetCheck(
        connectivityManager: ConnectivityManager,
    ): Boolean {
        val network = connectivityManager.activeNetwork
        val connection =
            connectivityManager.getNetworkCapabilities(network)

        return connection != null && (
                connection.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        connection.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

}