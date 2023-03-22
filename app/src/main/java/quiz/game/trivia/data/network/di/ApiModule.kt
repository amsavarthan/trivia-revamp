package quiz.game.trivia.data.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import quiz.game.trivia.BuildConfig
import quiz.game.trivia.data.network.api.QuizApi
import quiz.game.trivia.data.network.api.SessionTokenApi
import quiz.game.trivia.data.network.interceptors.NoConnectionInterceptor
import quiz.game.trivia.data.network.interceptors.SessionTokenInterceptor
import quiz.game.trivia.domain.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun providesRetrofit(
        noConnectionInterceptor: NoConnectionInterceptor,
        sessionTokenInterceptor: SessionTokenInterceptor
    ): Retrofit {
        val okHttpClient = OkHttpClient.Builder().run {
            if (BuildConfig.DEBUG) {
                val loggingInterceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(loggingInterceptor)
            }
            addInterceptor(noConnectionInterceptor)
            addInterceptor(sessionTokenInterceptor)
            build()
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun providesQuizApiAPI(
        retrofit: Retrofit,
    ): QuizApi {
        return retrofit.create()
    }

    @Singleton
    @Provides
    fun providesSessionTokenApiAPI(
        retrofit: Retrofit,
    ): SessionTokenApi {
        return retrofit.create()
    }

}