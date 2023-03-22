package quiz.game.trivia.domain.repository

import kotlinx.coroutines.flow.Flow
import quiz.game.trivia.domain.models.UserData

interface UserRepository {

    val userData: Flow<UserData>
    val googlePlayConnectedStatus: Flow<Boolean>
    val isEnergyAlertEnabled: Flow<Boolean>

    suspend fun updateGooglePlayConnectedStatus(isConnected: Boolean)
    suspend fun updateGooglePlayConnectedAccountData(userData: UserData)
    suspend fun updateEnergyAlertEnabledStatus(isEnabled: Boolean)

}