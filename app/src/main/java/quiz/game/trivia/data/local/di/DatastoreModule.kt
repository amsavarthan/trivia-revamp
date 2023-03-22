package quiz.game.trivia.data.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import quiz.game.trivia.GamePreference
import quiz.game.trivia.SessionTokenPreference
import quiz.game.trivia.UserPreference
import quiz.game.trivia.data.local.proto.GamePreferenceSerializer
import quiz.game.trivia.data.local.proto.SessionTokenPreferenceSerializer
import quiz.game.trivia.data.local.proto.UserPreferencesSerializer
import quiz.game.trivia.domain.GAME_PREFERENCE_STORE_FILE_NAME
import quiz.game.trivia.domain.SESSION_TOKEN_PREFERENCE_STORE_FILE_NAME
import quiz.game.trivia.domain.USER_PREFERENCE_STORE_FILE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    @Provides
    @Singleton
    fun provideSessionTokenPreferencesDatastore(
        @ApplicationContext context: Context,
        preferenceSerializer: SessionTokenPreferenceSerializer,
    ): DataStore<SessionTokenPreference> {
        return DataStoreFactory.create(
            serializer = preferenceSerializer,
            produceFile = { context.dataStoreFile(SESSION_TOKEN_PREFERENCE_STORE_FILE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideGamePreferencesDatastore(
        @ApplicationContext context: Context,
        preferenceSerializer: GamePreferenceSerializer,
    ): DataStore<GamePreference> {
        return DataStoreFactory.create(
            serializer = preferenceSerializer,
            produceFile = { context.dataStoreFile(GAME_PREFERENCE_STORE_FILE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideUserPreferencesDatastore(
        @ApplicationContext context: Context,
        preferenceSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreference> {
        return DataStoreFactory.create(
            serializer = preferenceSerializer,
            produceFile = { context.dataStoreFile(USER_PREFERENCE_STORE_FILE_NAME) }
        )
    }

}