package quiz.game.trivia.data.repository

import quiz.game.trivia.data.local.LocalDataSource
import quiz.game.trivia.data.network.NetworkDataSource
import quiz.game.trivia.data.network.model.Question
import quiz.game.trivia.domain.EMPTY_SESSION_TOKEN
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType
import quiz.game.trivia.domain.repository.QuestionsRepository
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultQuestionsRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val networkDataSource: NetworkDataSource,
) : QuestionsRepository {

    override suspend fun getQuestions(
        amount: Int,
        categoryId: Int,
        difficulty: Difficulty,
        questionType: QuestionType,
        shouldTryWithToken: Boolean,
    ) = networkDataSource.getQuestions(
        amount = amount,
        categoryId = categoryId,
        difficulty = difficulty,
        questionType = questionType,
        shouldTryWithToken = shouldTryWithToken
    ).questions.map { question ->
        Question(
            category = question.category.decode(),
            type = question.type.decode(),
            difficulty = question.difficulty.decode(),
            question = question.question.decode(),
            correctAnswer = question.correctAnswer.decode(),
            incorrectAnswers = question.incorrectAnswers.map { it.decode() },
        )
    }

    private fun String.decode() = URLDecoder.decode(this, Charsets.UTF_8.name())

    override suspend fun generateSessionToken(): Boolean {
        val response = networkDataSource.fetchSessionToken()
        if (response.responseCode != 0 || response.token.isNullOrBlank()) return false
        localDataSource.updateSessionToken(response.token)
        return true
    }

    override suspend fun resetSessionToken(): Boolean {
//        val response = networkDataSource.resetSessionToken()
//        if (response.responseCode != 0 || response.token.isNullOrBlank()) return false
        localDataSource.updateSessionToken(EMPTY_SESSION_TOKEN)
        return true
    }
}