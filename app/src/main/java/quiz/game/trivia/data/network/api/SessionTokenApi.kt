package quiz.game.trivia.data.network.api

import quiz.game.trivia.data.network.annotations.RequiresSessionToken
import quiz.game.trivia.data.network.model.TokenResponse
import retrofit2.http.GET

interface SessionTokenApi {

    @GET("/api_token.php?command=request")
    suspend fun fetchSessionToken(): TokenResponse

    @RequiresSessionToken
    @GET("/api_token.php?command=reset")
    suspend fun resetSessionToken(): TokenResponse

}