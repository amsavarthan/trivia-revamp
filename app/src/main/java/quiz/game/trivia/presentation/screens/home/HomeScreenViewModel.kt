package quiz.game.trivia.presentation.screens.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    gameRepository: GameRepository,
    userRepository: UserRepository
) : ViewModel() {

    private val shouldAskReview = savedStateHandle.getStateFlow("askReview", false)
    private val shouldRefreshScore = savedStateHandle.getStateFlow("refreshScore", true)

    val uiState = combine(
        gameRepository.energy,
        userRepository.googlePlayConnectedStatus,
        shouldRefreshScore,
        shouldAskReview
    ) { energy, isGooglePlayGamesConnected, shouldRefreshScore, shouldAskReview ->
        HomeScreenUiState(
            energy = energy,
            shouldAskReview = shouldAskReview,
            shouldRefreshScore = shouldRefreshScore,
            isGooglePlayConnected = isGooglePlayGamesConnected
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenUiState()
    )

    fun onReviewAsked() {
        savedStateHandle["askReview"] = false
    }

    fun onScoreRefreshed() {
        savedStateHandle["refreshScore"] = false
    }

}