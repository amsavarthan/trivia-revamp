package quiz.game.trivia.domain.models

import androidx.annotation.Keep

@Keep
data class Category(
    val id: Int,
    val name: String,
    val emoji: String,
    val forPro: Boolean = false
)

val categories = arrayOf(
    Category(9, "General Knowledge", "🤓"),
    Category(19, "Mathematics", "📝"),
    Category(21, "Sports", "🏀"),
    Category(10, "Books", "📚"),
    Category(18, "Computers", "💻"),
    Category(17, "Science & Nature", "🌱"),

    Category(11, "Film", "🎬", true),
    Category(12, "Music", "🎵", true),
    Category(14, "Television", "📺", true),
    Category(15, "Video Games", "🎮", true),
    Category(13, "Musicals & Theatres", "🎷", true),
    Category(16, "Board Games", "🎲", true),

//    Category(20, "Mythology", "🐲", true),
//    Category(22, "Geography", "🗺", true),
//    Category(25, "Art", "🎨", true),
//    Category(27, "Animals", "🦄", true),
//    Category(28, "Vehicles", "🚗", true),
//    Category(29, "Comics", "📰", true),
//    Category(31, "Japanese Anime & Manga", "🎎", true),
//    Category(32, "Cartoon & Animations", "🤡", true),
)
