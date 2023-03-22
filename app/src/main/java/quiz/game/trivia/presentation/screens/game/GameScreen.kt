package quiz.game.trivia.presentation.screens.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import quiz.game.trivia.data.network.model.Question
import quiz.game.trivia.presentation.ads.showInterstitial
import quiz.game.trivia.presentation.anim.SlideDirection
import quiz.game.trivia.presentation.anim.SlideOnChange
import quiz.game.trivia.presentation.navigation.AppScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun GameScreen(
    navController: NavController,
    viewModel: GameScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    var shouldShowQuitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    if (uiState.isError) {
        LaunchedEffect(snackBarHostState) {
            val snackBarResult = snackBarHostState.showSnackbar(
                message = uiState.errorMessage,
                actionLabel = "RETRY"
            )
            if (snackBarResult == SnackbarResult.ActionPerformed) {
                viewModel.onErrorConsumed()
                viewModel.fetchQuestions()
            }
        }
    }

    if (shouldShowQuitDialog) {
        AlertDialog(
            onDismissRequest = { shouldShowQuitDialog = false },
            title = { Text(text = "Stop Playing") },
            text = { Text(text = "Are you sure do want to stop playing? You will lose all your progress.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        shouldShowQuitDialog = false
                        navController.navigate(AppScreen.HOME.route.format(false, false)) {
                            launchSingleTop = true
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                        if (!uiState.isLoading) showInterstitial(context)
                    }
                ) {
                    Text(text = "Yes")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { shouldShowQuitDialog = false }
                ) {
                    Text(text = "No")
                }
            }
        )
    }

    if(!uiState.isGameStarted) {
        LaunchedEffect(Unit) {
            viewModel.fetchQuestions()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackBarHostState) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isNoQuestionFound) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No questions found",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "We couldn't find any question right now. Please try again later or try changing the difficulty from the Settings.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(36.dp))
                    Button(onClick = {
                        navController.navigate(AppScreen.SETTINGS.destination) {
                            launchSingleTop = true
                        }
                    }) {
                        Text(text = "Go to Settings")
                    }
                }
            } else {
                AnimatedVisibility(
                    visible = uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(150.dp)
                                .animateEnterExit()
                        )
                    }
                }
                AnimatedVisibility(
                    visible = !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    GameLayout(
                        question = uiState.question[uiState.currentQuestionIndex],
                        questionNumber = uiState.currentQuestionIndex.inc(),
                        onTimeOut = { isGameCompleted, selectedAnswer ->
                            viewModel.calculatePoints(selectedAnswer)
                            if (!isGameCompleted) {
                                viewModel.nextQuestion()
                                return@GameLayout
                            }

                            val result = viewModel.getResults()
                            navController.navigate(AppScreen.RESULT.route.format(result)) {
                                launchSingleTop = true
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                        isLastQuestion = viewModel::isLastQuestion
                    )
                }
            }
        }
    }

    BackHandler {
        shouldShowQuitDialog = true
    }

}

@Composable
fun GameLayout(
    question: Question,
    questionNumber: Int,
    isLastQuestion: () -> Boolean,
    onTimeOut: (Boolean, String?) -> Unit
) {

    val answers = remember(questionNumber) {
        (question.incorrectAnswers + question.correctAnswer).shuffled()
    }

    var selectedAnswerIndex by remember(questionNumber) {
        mutableStateOf(-1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Timer(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onTimeout = { isGameCompleted ->
                    onTimeOut(
                        isGameCompleted,
                        if (selectedAnswerIndex == -1) null else answers[selectedAnswerIndex]
                    )
                },
                isLastQuestion = isLastQuestion
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "QUESTION $questionNumber",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 2.sp
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            SlideOnChange(
                targetState = questionNumber,
                direction = SlideDirection.Adaptive
            ) {
                Text(
                    text = question.question,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnswersLayout(
            modifier = Modifier.weight(1f),
            answers = answers,
            selectedIndex = selectedAnswerIndex,
            onClick = { selectedAnswerIndex = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }

}

@Composable
private fun Timer(
    modifier: Modifier = Modifier,
    onTimeout: (Boolean) -> Unit,
    isLastQuestion: () -> Boolean
) {

    var restartFlag by remember { mutableStateOf(false) }
    var progressValue by remember { mutableStateOf(1.1f) }

    val progressAnimationValue by animateFloatAsState(
        targetValue = progressValue,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    LaunchedEffect(restartFlag) {
        while (progressValue > 0f) {
            progressValue -= 0.06f
            delay(500)
        }

        if (isLastQuestion()) {
            onTimeout(true)
            return@LaunchedEffect
        }

        onTimeout(false)
        delay(200)
        progressValue = 1.1f
        restartFlag = !restartFlag
    }

    LinearProgressIndicator(
        modifier = modifier
            .clip(CircleShape)
            .fillMaxWidth(),
        progress = progressAnimationValue,
    )

}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnswersLayout(
    modifier: Modifier = Modifier,
    answers: List<String>,
    selectedIndex: Int,
    onClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Bottom)
    ) {
        itemsIndexed(answers) { index, answer ->

            val transition = updateTransition(
                targetState = index == selectedIndex,
                label = "Answer Item Transitions"
            )

            val percent by transition.animateInt(
                label = "Answer Item Corner",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> 25
                        else -> 50
                    }
                })

            val background by transition.animateColor(
                label = "Answer Item Background",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    }
                }
            )

            val textColor by transition.animateColor(
                label = "Answer Item Text Color",
                targetValueByState = { targetState ->
                    when (targetState) {
                        true -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentHeight()
                    .heightIn(min = 70.dp)
                    .clip(RoundedCornerShape(percent))
                    .background(background)
                    .clickable(onClick = { onClick(index) })
                    .padding(16.dp)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = answer,
                    transitionSpec = { fadeIn() with fadeOut() }) { text ->
                    Text(
                        text = text,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                }
            }
        }
    }
}