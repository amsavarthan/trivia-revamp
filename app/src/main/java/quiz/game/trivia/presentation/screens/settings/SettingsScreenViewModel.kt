package quiz.game.trivia.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository,
) : ViewModel() {

    private val isChoosingQuestionType = MutableStateFlow(false)
    private val isChoosingDifficulty = MutableStateFlow(false)

    val uiState = combine(
        gameRepository.gameConfig,
        userRepository.googlePlayConnectedStatus,
        userRepository.isEnergyAlertEnabled,
        isChoosingQuestionType,
        isChoosingDifficulty
    ) { gameConfig, isConnectedToGooglePlayGames, isEnergyAlertEnabled, isChoosingQuestionType, isChoosingDifficulty ->
        SettingsScreenUiState(
            gameConfig = gameConfig,
            isEnergyAlertEnabled = isEnergyAlertEnabled,
            isConnectedToGooglePlayGames = isConnectedToGooglePlayGames,
            isChoosingDifficulty = isChoosingDifficulty,
            isChoosingQuestionType = isChoosingQuestionType
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsScreenUiState()
    )

    fun toggleIsDifficultyChoosing() {
        isChoosingDifficulty.update { !it }
    }

    fun toggleIsQuestionTypeChoosing() {
        isChoosingQuestionType.update { !it }
    }

    fun updateEnergyAlertEnabledStatus(isEnabled: Boolean) {
        viewModelScope.launch {
            userRepository.updateEnergyAlertEnabledStatus(isEnabled)
        }
    }

    fun updateDifficulty(difficulty: Difficulty) {
        viewModelScope.launch {
            gameRepository.updateDifficulty(difficulty)
        }
    }

    fun updateQuestionType(questionType: QuestionType) {
        viewModelScope.launch {
            gameRepository.updateQuestionType(questionType)
        }
    }

}