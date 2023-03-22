package quiz.game.trivia.data.repository

import androidx.core.net.toUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import quiz.game.trivia.data.local.LocalDataSource
import quiz.game.trivia.domain.models.UserData
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultUserRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
) : UserRepository {

    override val userData: Flow<UserData>
        get() = localDataSource.userPreferenceData.map { pref ->
            UserData(
                name = pref.googlePlayName,
                profilePic = pref.googlePlayPhoto.toUri()
            )
        }

    override val googlePlayConnectedStatus: Flow<Boolean>
        get() = localDataSource.userPreferenceData.map { it.isGooglePlayConnected }

    override val isEnergyAlertEnabled: Flow<Boolean>
        get() = localDataSource.userPreferenceData.map { it.isEnergyAlertEnabled }

    override suspend fun updateGooglePlayConnectedStatus(isConnected: Boolean) {
        localDataSource.updateUserPreference(isGooglePlayConnected = isConnected)
    }

    override suspend fun updateGooglePlayConnectedAccountData(userData: UserData) {
        localDataSource.updateUserPreference(userData = userData)
    }

    override suspend fun updateEnergyAlertEnabledStatus(isEnabled: Boolean) {
        localDataSource.updateUserPreference(isEnergyFillAlertEnabled = isEnabled)
    }

}