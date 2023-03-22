package quiz.game.trivia.domain.repository

import kotlinx.coroutines.flow.Flow
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.GameConfig
import quiz.game.trivia.domain.models.QuestionType

interface GameRepository {

    val energy: Flow<Int>
    val gameConfig: Flow<GameConfig>

    suspend fun decreaseEnergy()
    suspend fun increaseEnergy(earnedEnergy: Int = 1)
    suspend fun updateDifficulty(difficulty: Difficulty)
    suspend fun updateQuestionType(questionType: QuestionType)

}