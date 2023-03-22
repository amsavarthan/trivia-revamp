package quiz.game.trivia.presentation.navigation

enum class AppScreen(
    val destination: String,
    val route: String = destination,
) {
    CONNECT_GOOGLE_PLAY("connect_google_play"),
    HOME("home?askReview={askReview}&refreshScore={refreshScore}", route = "home?askReview=%b&refreshScore=%b"),
    CHOOSE_MODE("choose_mode"),
    QUICK_MODE("quick_mode"),
    CASUAL_MODE("casual_mode"),
    GAME("game/{category}", route = "game/%d"),
    RESULT("result/{answers}", route = "result/%s"),
    COUNT_DOWN("count-down/{category}", route = "count-down/%d"),
    SETTINGS("settings");
}
