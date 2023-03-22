package quiz.game.trivia.domain.repository

import quiz.game.trivia.data.network.model.Question
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType

interface QuestionsRepository {

    suspend fun getQuestions(
        amount: Int,
        categoryId: Int,
        difficulty: Difficulty,
        questionType: QuestionType,
        shouldTryWithToken: Boolean = true,
    ): List<Question>

    suspend fun generateSessionToken(): Boolean

    suspend fun resetSessionToken(): Boolean

}