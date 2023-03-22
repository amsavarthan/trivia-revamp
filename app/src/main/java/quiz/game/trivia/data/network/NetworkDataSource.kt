package quiz.game.trivia.data.network

import quiz.game.trivia.data.network.api.QuizApi
import quiz.game.trivia.data.network.api.SessionTokenApi
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkDataSource @Inject constructor(
    private val sessionTokenApi: SessionTokenApi,
    private val quizApi: QuizApi
) {

    suspend fun getQuestions(
        amount: Int,
        categoryId: Int,
        difficulty: Difficulty,
        questionType: QuestionType,
        shouldTryWithToken: Boolean,
    ) = if (shouldTryWithToken) {
        quizApi.getQuestions(
            amount = amount,
            categoryId = categoryId,
            difficulty = difficulty.apiValue,
            type = questionType.apiValue,
        )
    } else {
        quizApi.getQuestionsWithoutSessionToken(
            amount = amount,
            categoryId = categoryId,
            difficulty = difficulty.apiValue,
            type = questionType.apiValue,
        )
    }

    suspend fun resetSessionToken() = sessionTokenApi.resetSessionToken()

    suspend fun fetchSessionToken() = sessionTokenApi.fetchSessionToken()

    fun increaseTriviaPoints() {
        TODO()
    }

    fun getUserPoints(): Int {
        TODO()
    }

}