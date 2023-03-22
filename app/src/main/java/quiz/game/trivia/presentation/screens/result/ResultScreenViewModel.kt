package quiz.game.trivia.presentation.screens.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import quiz.game.trivia.domain.EASY_CORRECT_ANSWER_POINT_MULTIPLIER
import quiz.game.trivia.domain.HARD_CORRECT_ANSWER_POINT_MULTIPLIER
import quiz.game.trivia.domain.MEDIUM_CORRECT_ANSWER_POINT_MULTIPLIER
import quiz.game.trivia.domain.STREAK_POINT_MULTIPLIER
import quiz.game.trivia.domain.models.AnswerType
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.decode
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject
import kotlin.math.max


@HiltViewModel
class ResultScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    userRepository: UserRepository,
) : ViewModel() {

    private val answersResult = savedStateHandle.get<String>("answers")?.decode().orEmpty()

    private val viewModelState = MutableStateFlow(ResultScreenUiState())
    val uiState = viewModelState.asStateFlow()

    val isConnectedWithGooglePlayGames = userRepository.googlePlayConnectedStatus
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    private fun getPointMultiplierForDifficulty(difficulty: Difficulty): Int {
        return when (difficulty) {
            Difficulty.MEDIUM -> MEDIUM_CORRECT_ANSWER_POINT_MULTIPLIER
            Difficulty.HARD -> HARD_CORRECT_ANSWER_POINT_MULTIPLIER
            else -> EASY_CORRECT_ANSWER_POINT_MULTIPLIER
        }
    }

    fun getStreakCount(): Long {
        val answerTypes = answersResult.map { (answerType, _) -> answerType }
        return calculateStreakCount(answerTypes)
    }

    fun getPoints(): Long {
        val pointsForCorrectAnswerByDifficulty = answersResult
            .filter { (answerType, _) -> answerType == AnswerType.CORRECT }
            .sumOf { (_, difficulty) ->
                getPointMultiplierForDifficulty(difficulty = difficulty)
            }
        return (getStreakCount() * STREAK_POINT_MULTIPLIER).plus(pointsForCorrectAnswerByDifficulty)
    }

    fun calculatePoints() {
        viewModelScope.launch {
            viewModelState.update { oldState ->

                val answerTypes = answersResult.map { (answerType, _) -> answerType }
                val correctAnswersCount = answerTypes.count { it == AnswerType.CORRECT }.toLong()
                val streakCount = getStreakCount()
                val points = getPoints()

                oldState.copy(
                    correctCount = correctAnswersCount,
                    incorrectCount = answerTypes.count { it == AnswerType.INCORRECT }.toLong(),
                    missedCount = answerTypes.count { it == AnswerType.MISSED }.toLong(),
                    streakCount = streakCount,
                    points = points,
                )
            }
        }
    }

    fun makeStatsVisible() {
        viewModelState.update { it.copy(canShowStats = true) }
    }

    private fun calculateStreakCount(answers: List<AnswerType>): Long {
        var maxStreak = -1L
        var streak = -1L
        answers.forEach {
            streak = if (it == AnswerType.CORRECT) streak.inc() else -1L
            if (streak > maxStreak) maxStreak = streak
        }
        return max(0L, maxStreak)
    }

}
