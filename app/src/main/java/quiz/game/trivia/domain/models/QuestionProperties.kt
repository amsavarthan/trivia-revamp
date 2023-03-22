package quiz.game.trivia.domain.models


enum class Difficulty(
    private val value: String,
    val emoji: String,
    val apiValue: String? = value.lowercase(),
) {
    ANY("Any", "🤷", null),
    EASY("Easy", "😁"),
    MEDIUM("Medium", "🤨"),
    HARD("Hard", "🥵");

    override fun toString() = value

    companion object {
        fun toEnum(value: String) = Difficulty.values().first {
            it.value.lowercase() == value.lowercase()
        }
    }

}

enum class QuestionType(
    private val value: String,
    val emoji: String,
    val apiValue: String?
) {
    ANY("Any", "🤷", null),
    TRUE_FALSE("True/False", "✌️", "boolean"),
    MULTIPLE("Multiple Choice", "📝", "multiple");

    override fun toString() = value
}
