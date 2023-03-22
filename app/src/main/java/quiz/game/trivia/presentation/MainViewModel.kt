package quiz.game.trivia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import quiz.game.trivia.domain.models.UserData
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val isConnectingToGooglePlay = MutableStateFlow(true)

    val energyCount = gameRepository.energy
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = -1
        )

    val uiState = combine(
        userRepository.userData,
        userRepository.googlePlayConnectedStatus,
        isConnectingToGooglePlay
    ) { userData, isGooglePlayGamesConnected, isConnectingToGooglePlayGames ->
        MainUiState(
            userData = userData,
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