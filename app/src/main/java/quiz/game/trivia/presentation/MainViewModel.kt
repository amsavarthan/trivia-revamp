package quiz.game.trivia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import quiz.game.trivia.domain.models.UserData
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val isConnectingToGooglePlay = MutableStateFlow(true)

    val uiState = combine(
        userRepository.userData,
        userRepository.googlePlayConnectedStatus,
        isConnectingToGooglePlay,
        gameRepository.energy
    ) { userData, isGooglePlayGamesConnected, isConnectingToGooglePlayGames, energyCount ->
        MainUiState(
            userData = userData,
            energyCount = energyCount,
            isGooglePlayGamesConnected = isGooglePlayGamesConnected,
            isConnectingToGooglePlayGames = isConnectingToGooglePlayGames
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainUiState()
    )

    fun updateGooglePlayConnectedStatus(isConnected: Boolean) {
        viewModelScope.launch {
            userRepository.updateGooglePlayConnectedStatus(isConnected)
        }
    }

    fun updateGooglePlayConnectedAccountData(userData: UserData) {
        viewModelScope.launch {
            userRepository.updateGooglePlayConnectedAccountData(userData)
        }
    }


    fun updateGooglePlayConnectingStatus(isConnecting: Boolean) {
        isConnectingToGooglePlay.update { isConnecting }
    }
}