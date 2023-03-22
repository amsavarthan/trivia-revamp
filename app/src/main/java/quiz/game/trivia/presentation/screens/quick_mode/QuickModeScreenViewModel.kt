package quiz.game.trivia.presentation.screens.quick_mode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import quiz.game.trivia.domain.repository.GameRepository
import javax.inject.Inject

@HiltViewModel
class QuickModeScreenViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    val isEnergySufficient = gameRepository.energy
        .map { it > 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun increaseEnergy(earnedEnergy: Int) {
        viewModelScope.launch {
            gameRepository.increaseEnergy(earnedEnergy)
        }
    }

}