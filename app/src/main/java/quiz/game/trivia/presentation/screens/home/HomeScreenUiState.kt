package quiz.game.trivia.presentation.screens.home

import androidx.annotation.Keep


@Keep
data class HomeScreenUiState(
    val energy: Int? = null,
    val shouldAskReview: Boolean = false,
    val shouldRefreshScore: Boolean = true,
    val isGooglePlayConnected: Boolean = false
)