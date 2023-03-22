package quiz.game.trivia.domain.models

import android.net.Uri
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import quiz.game.trivia.domain.DEFAULT_PLAYER_NAME

@Keep
@Parcelize
data class UserData(
    val name: String = DEFAULT_PLAYER_NAME,
    val profilePic: Uri? = null,
) : Parcelable
