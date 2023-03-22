package quiz.game.trivia.data.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class QuestionsResponse(
    @SerializedName("response_code") val responseCode: Int,
    @SerializedName("results") val questions: List<Question>
)

@Keep
data class Question(
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    @SerializedName("correct_answer") val correctAnswer: String,
    @SerializedName("incorrect_answers") val incorrectAnswers: List<String>
)
