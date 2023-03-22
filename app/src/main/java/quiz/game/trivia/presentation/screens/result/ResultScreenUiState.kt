package quiz.game.trivia.presentation.screens.result

import androidx.annotation.Keep

@Keep
data class ResultScreenUiState(
    val correctCount: Long = 0,
    val incorrectCount: Long = 0,
    val missedCount: Long = 0,
    val streakCount: Long = 0,
    val points: Long = 0,
    val canShowStats: Boolean = false
)
