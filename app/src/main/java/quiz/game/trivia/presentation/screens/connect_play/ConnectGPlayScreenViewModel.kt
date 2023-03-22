package quiz.game.trivia.presentation.screens.connect_play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ConnectGPlayScreenViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {

    private val isConnecting = MutableStateFlow(false)

    val uiState = combine(
        userRepository.googlePlayConnectedStatus,
        isConnecting,
    ) { isGooglePlayConnected, isConnecting ->
        ConnectGPlayScreenUiState(
            isGooglePlayConnected = isGooglePlayConnected,
            isConnecting = isConnecting,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConnectGPlayScreenUiState()
    )

    fun updateConnectingStatus(isConnecting: Boolean) {
        this.isConnecting.update { isConnecting }
    }

}