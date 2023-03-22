package quiz.game.trivia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import quiz.game.trivia.data.repository.DefaultGameRepository
import quiz.game.trivia.data.repository.DefaultQuestionsRepository
import quiz.game.trivia.data.repository.DefaultUserRepository
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.QuestionsRepository
import quiz.game.trivia.domain.repository.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Singleton
    @Binds
    fun bindsQuestionsRepository(
        questionsRepository: DefaultQuestionsRepository
    ): QuestionsRepository

    @Singleton
    @Binds
    fun bindsGameRepository(
        gameRepository: DefaultGameRepository
    ): GameRepository

    @Singleton
    @Binds
    fun bindsUserRepository(
        userRepository: DefaultUserRepository
    ): UserRepository

}