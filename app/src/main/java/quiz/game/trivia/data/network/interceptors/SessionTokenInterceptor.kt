package quiz.game.trivia.data.network.interceptors

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import quiz.game.trivia.data.local.LocalDataSource
import quiz.game.trivia.data.network.annotations.RequiresSessionToken
import quiz.game.trivia.data.utils.NetworkException
import quiz.game.trivia.domain.CODE_NOT_ENOUGH_QUESTION
import quiz.game.trivia.domain.CODE_TOKEN_EMPTY
import quiz.game.trivia.domain.CODE_TOKEN_NOT_FOUND
import quiz.game.trivia.domain.EMPTY_SESSION_TOKEN
import retrofit2.Invocation
import javax.inject.Inject

class SessionTokenInterceptor @Inject constructor(
    private val localDataSource: LocalDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val isGooglePlayGamesConnected = runBlocking {
            localDataSource.userPreferenceData.first().isGooglePlayConnected
        }

        if (!isGooglePlayGamesConnected) {
            runBlocking { localDataSource.updateSessionToken(EMPTY_SESSION_TOKEN) }
            return chain.proceed(request)
        }

        val invocation = chain.request().tag(Invocation::class.java)
            ?: return chain.proceed(request)

        val shouldAttachToken = invocation
            .method()
            .annotations
            .any { it.annotationClass == RequiresSessionToken::class }

        if (!shouldAttachToken) {
            val response = chain.proceed(request)

            if (response.isSuccessful && response.body != null) {
                runCatching {
                    val body = response.peekBody(Long.MAX_VALUE).string()
                    val jsonResponse = JSONObject(body)
                    val errorCode = jsonResponse.getInt("response_code")
                    if (errorCode == CODE_NOT_ENOUGH_QUESTION) {
                        throw NetworkException.NotEnoughQuestionException
                    }
                }.onFailure { exception ->
                    if (exception is NetworkException) throw exception
                    Firebase.crashlytics.log("Failure in SessionTokenInterceptor: $exception")
                }
            }

            return response
        }

        val token = runBlocking { localDataSource.sessionTokenData.first().token }
        if (token.isNullOrBlank()) throw NetworkException.NoSessionTokenException

        val requestUrlWithToken = request.url.newBuilder()
            .addQueryParameter("token", token)
            .build()

        val newRequest = request.newBuilder()
            .url(requestUrlWithToken)
            .build()

        val response = chain.proceed(newRequest)

        if (response.isSuccessful && response.body != null) {
            runCatching {
                val body = response.peekBody(Long.MAX_VALUE).string()
                val jsonResponse = JSONObject(body)
                when (jsonResponse.getInt("response_code")) {
                    CODE_NOT_ENOUGH_QUESTION -> throw NetworkException.NotEnoughQuestionException
                    CODE_TOKEN_NOT_FOUND -> throw NetworkException.NoSessionTokenException
                    CODE_TOKEN_EMPTY -> throw NetworkException.SessionTokenRefreshRequiredException
                    else -> Unit
                }
            }.onFailure { exception ->
                if (exception is NetworkException) throw exception
                Firebase.crashlytics.log("Failure in SessionTokenInterceptor: $exception")
            }
        }

        return response
    }
}