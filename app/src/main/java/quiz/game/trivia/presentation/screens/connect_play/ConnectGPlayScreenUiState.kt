package quiz.game.trivia.presentation.screens.connect_play

import androidx.annotation.Keep

@Keep
data class ConnectGPlayScreenUiState(
    val isGooglePlayConnected: Boolean = false,
    val isConnecting: Boolean = false
)