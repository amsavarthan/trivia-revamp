package quiz.game.trivia.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import quiz.game.trivia.data.local.LocalDataSource
import quiz.game.trivia.domain.MAX_ENERGY
import quiz.game.trivia.domain.MIN_ENERGY
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.GameConfig
import quiz.game.trivia.domain.models.QuestionType
import quiz.game.trivia.domain.repository.GameRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultGameRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
) : GameRepository {

    override val gameConfig: Flow<GameConfig>
        get() = localDataSource.gamePreferenceData.map {
            GameConfig(
                difficulty = Difficulty.values()[it.gameDifficulty],
                type = QuestionType.values()[it.gameType],
                questionCount = it.questionsCount,
            )
        }

    override val energy: Flow<Int>
        get() = localDataSource.gamePreferenceData.map { it.energy }

    override suspend fun decreaseEnergy() {
        val currentEnergy = localDataSource.gamePreferenceData.first().energy
        localDataSource.updateGamePreference(
            energy = currentEnergy.dec().coerceAtLeast(MIN_ENERGY)
        )
    }

    override suspend fun increaseEnergy(earnedEnergy: Int) {
        val currentEnergy = localDataSource.gamePreferenceData.first().energy
        localDataSource.updateGamePreference(
            energy = (currentEnergy + earnedEnergy).coerceAtMost(MAX_ENERGY)
        )
    }

    override suspend fun updateDifficulty(difficulty: Difficulty) {
        localDataSource.updateGamePreference(difficulty = difficulty)
    }

    override suspend fun updateQuestionType(questionType: QuestionType) {
        localDataSource.updateGamePreference(questionType = questionType)
    }

}