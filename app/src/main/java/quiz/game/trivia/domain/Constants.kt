package quiz.game.trivia.domain

//Notification
const val CHANNEL_ID = "energy"

//Datastore
const val SESSION_TOKEN_PREFERENCE_STORE_FILE_NAME = "session_token.proto"
const val GAME_PREFERENCE_STORE_FILE_NAME = "game_preference.proto"
const val USER_PREFERENCE_STORE_FILE_NAME = "user_preference.proto"

//Network
const val BASE_URL = "https://opentdb.com"

const val CODE_TOKEN_NOT_FOUND = 3
const val CODE_TOKEN_EMPTY = 4
const val CODE_NOT_ENOUGH_QUESTION = 1

const val EMPTY_SESSION_TOKEN = ""

//Game Preference
const val DEFAULT_PLAYER_NAME = "CHAMP"

const val DEFAULT_QUESTIONS_COUNT = 5
const val DEBUG_QUESTIONS_COUNT = 2

const val INTERVAL_MINUTES = 5

const val MIN_ENERGY = 0
const val MAX_ENERGY = 5

const val STREAK_POINT_MULTIPLIER = 5
const val EASY_CORRECT_ANSWER_POINT_MULTIPLIER = 2
const val MEDIUM_CORRECT_ANSWER_POINT_MULTIPLIER = 3
const val HARD_CORRECT_ANSWER_POINT_MULTIPLIER = 4
