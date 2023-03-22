package quiz.game.trivia.presentation

import androidx.annotation.Keep
import quiz.game.trivia.domain.models.UserData

@Keep
data class MainUiState(
    val userData: UserData = UserData(),
    val isGooglePlayGamesConnected: Boolean = false,
    val isConnectingToGooglePlayGames: Boolean = true,
)