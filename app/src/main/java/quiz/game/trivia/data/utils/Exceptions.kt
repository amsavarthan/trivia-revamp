package quiz.game.trivia.data.utils

import java.io.IOException

sealed interface GameExceptions

sealed class NetworkException : IOException(), GameExceptions {
    object NoConnectionException : NetworkException()
    object NoSessionTokenException : NetworkException()
    object SessionTokenRefreshRequiredException : NetworkException()
    object NotEnoughQuestionException : NetworkException()
}
