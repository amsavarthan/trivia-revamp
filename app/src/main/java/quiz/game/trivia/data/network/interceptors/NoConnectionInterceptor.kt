package quiz.game.trivia.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import quiz.game.trivia.data.utils.NetworkManager
import javax.inject.Inject

class NoConnectionInterceptor @Inject constructor(
    private val networkManager: NetworkManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return networkManager.withInternetConnection {
            chain.proceed(request)
        }
    }
}