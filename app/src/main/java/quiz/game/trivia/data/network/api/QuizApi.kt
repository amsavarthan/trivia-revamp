package quiz.game.trivia.data.network.api

import quiz.game.trivia.data.network.annotations.RequiresSessionToken
import quiz.game.trivia.data.network.model.QuestionsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuizApi {

    @RequiresSessionToken
    @GET("/api.php")
    suspend fun getQuestions(
        @Query("amount") amount: Int,
        @Query("category") categoryId: Int,
        @Query("type") type: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("encode") encode: String = "url3986",
    ): QuestionsResponse

    @GET("/api.php")
    suspend fun getQuestionsWithoutSessionToken(
        @Query("amount") amount: Int,
        @Query("category") categoryId: Int,
        @Query("type") type: String? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("encode") encode: String = "url3986",
    ): QuestionsResponse

}