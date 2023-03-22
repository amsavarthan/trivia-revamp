package quiz.game.trivia.presentation.screens.game

import androidx.annotation.Keep
import quiz.game.trivia.data.network.model.Question

@Keep
data class GameScreenUiState(
    val question: List<Question> = emptyList(),
    val currentQuestionIndex: Int = -1,
    val errorMessage: String = "",
    val isError: Boolean = false,
    val isNoQuestionFound: Boolean = false,
    val isLoading: Boolean = true,
    val isGameStarted: Boolean = false,
)