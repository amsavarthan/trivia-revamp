package quiz.game.trivia.presentation.screens.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import quiz.game.trivia.BuildConfig
import quiz.game.trivia.data.network.model.Question
import quiz.game.trivia.data.utils.NetworkException
import quiz.game.trivia.domain.DEBUG_QUESTIONS_COUNT
import quiz.game.trivia.domain.models.*
import quiz.game.trivia.domain.repository.GameRepository
import quiz.game.trivia.domain.repository.QuestionsRepository
import javax.inject.Inject

const val NO_ERROR = ""

@HiltViewModel
class GameScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val questionsRepository: QuestionsRepository,
    private val gameRepository: GameRepository
) : ViewModel() {

    @OptIn(SavedStateHandleSaveableApi::class)
    private var isGameStarted by savedStateHandle.saveable { mutableStateOf(false) }

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        if (throwable is NetworkException.NotEnoughQuestionException) {
            noQuestionFoundStatus.update { true }
            return@CoroutineExceptionHandler
        }

        val message = throwable.message ?: "Some error occurred"
        if (message.lowercase().contains("unable to resolve host")) {
            errorMessage.update { "No internet connection." }
            return@CoroutineExceptionHandler
        }
        if (message.lowercase().contains("timed out")) {
            errorMessage.update { "Questions could not be fetched at the moment." }
            return@CoroutineExceptionHandler
        }
        errorMessage.update { message }
    }

    private val categoryIndex = savedStateHandle.get<Int>("category") ?: 0

    private val questions = MutableStateFlow<List<Question>>(emptyList())
    private val noQuestionFoundStatus = MutableStateFlow(false)
    private val currentQuestionIndex = MutableStateFlow(-1)
    private val errorMessage = MutableStateFlow(NO_ERROR)

    val uiState = combine(
        questions,
        currentQuestionIndex,
        noQuestionFoundStatus,
        errorMessage
    ) { questions, currentQuestionIndex, isNoQuestionFound, errorMessage ->

        val isLoading = questions.isEmpty()
        if (!isLoading && !isGameStarted) {
            gameRepository.decreaseEnergy()
            isGameStarted = true
        }

        GameScreenUiState(
            question = questions,
            currentQuestionIndex = currentQuestionIndex,
            errorMessage = errorMessage,
            isError = errorMessage.isNotBlank(),
            isLoading = isLoading,
            isNoQuestionFound = isNoQuestionFound,
            isGameStarted = isGameStarted
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = GameScreenUiState()
    )

    fun fetchQuestions() {
        noQuestionFoundStatus.update { false }
        viewModelScope.launch(exceptionHandler) {
            //simulated delay for ux
            delay(250)
            questions.update { questions ->
                questions.ifEmpty {
                    getQuestions(categoryId = categories[categoryIndex].id)
                }
            }
            currentQuestionIndex.update { 0 }
        }
    }

    fun onErrorConsumed() {
        errorMessage.update { NO_ERROR }
    }

    fun isLastQuestion(): Boolean {
        val questionsSize = uiState.value.question.size
        val currentIndex = uiState.value.currentQuestionIndex
        return currentIndex + 1 == questionsSize
    }

    fun nextQuestion() {
        if (isNextQuestionAvailable()) currentQuestionIndex.update { it.inc() }
    }

    private fun isNextQuestionAvailable(): Boolean {
        val questionsSize = uiState.value.question.size
        val currentIndex = uiState.value.currentQuestionIndex
        return currentIndex + 1 < questionsSize
    }

    private val answerStateForQuestions = mutableListOf<Pair<AnswerType, Difficulty>>()

    fun calculatePoints(selectedAnswer: String?) {
        val indexOfPlayedQuestion = uiState.value.currentQuestionIndex
        val questionPlayed = uiState.value.question[indexOfPlayedQuestion]
        val answerType = when {
            selectedAnswer == null -> AnswerType.MISSED
            questionPlayed.correctAnswer == selectedAnswer -> AnswerType.CORRECT
            else -> AnswerType.INCORRECT
        }
        val difficulty = Difficulty.toEnum(questionPlayed.difficulty)
        answerStateForQuestions.add(Pair(answerType, difficulty))
    }

    fun getResults() = answerStateForQuestions.encode()

    private suspend fun getQuestions(
        categoryId: Int,
        shouldRetryIfFailed: Boolean = true,
        shouldTryWithToken: Boolean = true
    ): List<Question> {
        return runCatching {
            val gameConfig = gameRepository.gameConfig.first()
            val amount = if (BuildConfig.DEBUG) DEBUG_QUESTIONS_COUNT else gameConfig.questionCount
            questionsRepository.getQuestions(
                amount = amount,
                categoryId = categoryId,
                difficulty = gameConfig.difficulty,
                questionType = gameConfig.type,
                shouldTryWithToken = shouldTryWithToken
            )
        }.fold(
            onSuccess = { return@fold it },
            onFailure = { exception ->
                if (!shouldRetryIfFailed) {
                    if (exception is NetworkException.NotEnoughQuestionException) throw exception
                    throw Error("Some error occurred, please try again.")
                }
                when (exception) {
                    is NetworkException.NoConnectionException -> {
                        throw Error("No internet connection.")
                    }
                    is NetworkException.NoSessionTokenException -> {
                        val tokenGenerated = questionsRepository.generateSessionToken()
                        if (tokenGenerated) {
                            return@fold getQuestions(categoryId, false)
                        }
                    }
                    is NetworkException.SessionTokenRefreshRequiredException -> {
                        val tokenRefreshed = questionsRepository.resetSessionToken()
                        if (tokenRefreshed) {
                            return@fold getQuestions(
                                categoryId = categoryId,
                                shouldRetryIfFailed = false,
                                shouldTryWithToken = false
                            )
                        }
                    }
                    is NetworkException.NotEnoughQuestionException -> throw exception
                    else -> Unit
                }
                throw Error("Questions could not be fetched at the moment.")
            }
        )
    }

}