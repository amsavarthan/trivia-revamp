package quiz.game.trivia.data.local

import androidx.datastore.core.DataStore
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.catch
import quiz.game.trivia.GamePreference
import quiz.game.trivia.SessionTokenPreference
import quiz.game.trivia.UserPreference
import quiz.game.trivia.domain.DEFAULT_PLAYER_NAME
import quiz.game.trivia.domain.DEFAULT_QUESTIONS_COUNT
import quiz.game.trivia.domain.MAX_ENERGY
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType
import quiz.game.trivia.domain.models.UserData
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val sessionTokenPreference: DataStore<SessionTokenPreference>,
    private val gamePreference: DataStore<GamePreference>,
    private val userPreference: DataStore<UserPreference>,
) {

    val sessionTokenData = sessionTokenPreference.data
        .catch { exception ->
            if (exception is IOException) {
                emit(SessionTokenPreference.getDefaultInstance())
            } else {
                throw exception
            }
        }

    val gamePreferenceData = gamePreference.data
        .catch { exception ->
            if (exception is IOException) {
                emit(
                    GamePreference.getDefaultInstance()
                        .toBuilder()
                        .setEnergy(MAX_ENERGY)
                        .setQuestionsCount(DEFAULT_QUESTIONS_COUNT)
                        .build()
                )
            } else {
                throw exception
            }
        }

    val userPreferenceData = userPreference.data
        .catch { exception ->
            if (exception is IOException) {
                emit(
                    UserPreference.getDefaultInstance()
                        .toBuilder()
                        .setIsEnergyAlertEnabled(true)
                        .build()
                )
            } else {
                throw exception
            }
        }

    suspend fun updateSessionToken(token: String) {
        runCatching {
            sessionTokenPreference.updateData { currentPreference ->
                currentPreference.toBuilder()
                    .setToken(token)
                    .build()
            }
        }.onFailure {
            Firebase.crashlytics.log("Failure in updateSessionToken: $it")
        }
    }

    suspend fun updateGamePreference(
        energy: Int? = null,
        difficulty: Difficulty? = null,
        questionType: QuestionType? = null,
        questionsCount: Int? = null,
    ) {
        check(energy != null || difficulty != null || questionType != null || questionsCount != null ) {
            "All parameters cannot be null."
        }
        if (questionsCount != null) {
            check(questionsCount !in 5..10) {
                "Questions count should be in the range 5-10"
            }
        }
        runCatching {
            gamePreference.updateData { currentPreference ->
                val builder = currentPreference.toBuilder()

                if (difficulty != null) {
                    builder.gameDifficulty = difficulty.ordinal
                }
                if (questionType != null) {
                    builder.gameType = questionType.ordinal
                }
                if (questionsCount != null) {
                    builder.questionsCount = questionsCount
                }
                if (energy != null) {
                    builder.energy = energy
                }

                builder.build()
            }
        }.onFailure {
            Firebase.crashlytics.log("Failure in updateGamePreference: $it")
        }
    }

    suspend fun updateUserPreference(
        isGooglePlayConnected: Boolean? = null,
        isEnergyFillAlertEnabled: Boolean? = null,
        userData: UserData? = null,
    ) {
        check( isEnergyFillAlertEnabled != null || isGooglePlayConnected != null || userData != null) {
            "All parameters cannot be null."
        }
        runCatching {
            userPreference.updateData { currentPreference ->
                val builder = currentPreference.toBuilder()
                if (isGooglePlayConnected != null) {
                    builder.isGooglePlayConnected = isGooglePlayConnected
                }
                if (isEnergyFillAlertEnabled != null) {
                    builder.isEnergyAlertEnabled = isEnergyFillAlertEnabled
                }
                if (userData != null) {
                    builder.googlePlayName = userData.name
                    builder.googlePlayPhoto = userData.profilePic?.toString().orEmpty()
                }
                builder.build()
            }
        }.onFailure {
            Firebase.crashlytics.log("Failure in updateUserPreference: $it")
        }
    }

}