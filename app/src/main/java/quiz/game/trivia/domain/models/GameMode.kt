package quiz.game.trivia.domain.models

import androidx.annotation.Keep
import quiz.game.trivia.presentation.navigation.AppScreen

@Keep
data class GameMode(
    val emoji: String,
    val title: String,
    val description: String,
    val route: AppScreen,
)

val gameModes = listOf(
    GameMode(
        emoji = "⚡️",
        title = "Quick Mode",
        route = AppScreen.QUICK_MODE,
        description = "Play a game right away on category chosen randomly.",
    ),
    GameMode(
        emoji = "🤠",
        title = "Casual Mode",
        route = AppScreen.CASUAL_MODE,
        description = "You get to choose the category of your own choice.",
    )
)