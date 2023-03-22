package quiz.game.trivia.domain.models

import androidx.annotation.Keep
import quiz.game.trivia.domain.DEFAULT_QUESTIONS_COUNT

@Keep
data class GameConfig(
    val difficulty: Difficulty = Difficulty.ANY,
    val type: QuestionType = QuestionType.ANY,
    val questionCount: Int = DEFAULT_QUESTIONS_COUNT
)
