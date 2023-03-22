package quiz.game.trivia.domain.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class GameData(
    val rank: String? = null,
    val points: String? = null,
) : Parcelable
