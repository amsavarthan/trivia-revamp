package quiz.game.trivia.data.network.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenResponse(
    @SerializedName("response_code") val responseCode: Int,
    val token: String? = null
)