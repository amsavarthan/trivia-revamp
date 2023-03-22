package quiz.game.trivia.presentation.screens.settings

import androidx.annotation.Keep
import quiz.game.trivia.domain.models.GameConfig

@Keep
data class SettingsScreenUiState(
    val gameConfig: GameConfig = GameConfig(),
    val isEnergyAlertEnabled: Boolean = true,
    val isConnectedToGooglePlayGames: Boolean = true,
    val isChoosingDifficulty: Boolean = false,
    val isChoosingQuestionType: Boolean = false,
)