package quiz.game.trivia.domain.models

enum class AnswerType {
    INCORRECT, CORRECT, MISSED
}


//converts from - [(1,1),(0,1)...] into "11,01,..."
fun List<Pair<AnswerType, Difficulty>>.encode(): String {
    val stringBuilder = StringBuilder(size)
    forEach { (answerType, difficulty) ->
        stringBuilder.append(answerType.ordinal)
        stringBuilder.append(difficulty.ordinal)
        stringBuilder.append(",")
    }
    return stringBuilder.toString()
}

//converts from - "11,01,..." into [(1,1),(0,1)...]
fun String.decode(): List<Pair<AnswerType, Difficulty>> {
    val listBuilder = mutableListOf<Pair<AnswerType, Difficulty>>()
    split(",").filter { it.isNotBlank() }.forEach { str ->
        listBuilder.add(
            Pair(
                AnswerType.values()[str[0].digitToInt()],
                Difficulty.values()[str[1].digitToInt()]
            )
        )
    }
    return listBuilder.toList()
}