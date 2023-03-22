package quiz.game.trivia.presentation.screens.quick_mode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import quiz.game.trivia.domain.models.categories
import quiz.game.trivia.presentation.ads.showEnergyRewardAd
import quiz.game.trivia.presentation.ads.showRewardAd
import quiz.game.trivia.presentation.anim.SlideDirection
import quiz.game.trivia.presentation.anim.SlideOnChange
import quiz.game.trivia.presentation.composable.BackButton
import quiz.game.trivia.presentation.composable.Scaffold
import quiz.game.trivia.presentation.composable.TopBar
import quiz.game.trivia.presentation.navigation.AppScreen
import kotlin.random.Random

enum class SelectionState {
    RUNNING, COMPLETED
}

@Composable
fun QuickModeScreen(
    navController: NavController,
    viewModel: QuickModeScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val canStartGame by viewModel.isEnergySufficient.collectAsState()

    var selectionState by rememberSaveable {
        mutableStateOf(SelectionState.RUNNING)
    }

    var lookingIndex by rememberSaveable {
        mutableStateOf(Random.nextInt(categories.size))
    }

    var showDialogForProAd by remember {
        mutableStateOf(false)
    }

    var showDialogForEnergyAd by remember {
        mutableStateOf(false)
    }

    if (showDialogForEnergyAd) {
        AlertDialog(
            onDismissRequest = { showDialogForEnergyAd = false },
            title = { Text(text = "Insufficient Energy") },
            text = { Text(text = "You don't have sufficient energy to start the game. You can either wait or watch an Ad to continue.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialogForEnergyAd = false
                        showEnergyRewardAd(
                            context = context,
                            increaseEnergy = viewModel::increaseEnergy,
                            onEnergyIncreased = {
                                if (categories[lookingIndex].forPro) {
                                    showDialogForProAd = true
                                    return@showEnergyRewardAd
                                }
                                navController.navigate(
                                    route = AppScreen.COUNT_DOWN.route.format(lookingIndex)
                                ) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                ) {
                    Text(text = "Watch Ad")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialogForEnergyAd = false }
                ) {
                    Text(text = "Wait")
                }
            }
        )
    }

    if (showDialogForProAd) {
        AlertDialog(
            onDismissRequest = { showDialogForProAd = false },
            title = { Text(text = "Pro Required") },
            text = { Text(text = "To access the category you want to play, you need to have a PRO subscription. However, you can temporarily unlock it by watching an advertisement.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialogForProAd = false
                        showRewardAd(context) {
                            navController.navigate(AppScreen.COUNT_DOWN.route.format(lookingIndex)) {
                                launchSingleTop = true
                            }
                        }
                    }
                ) {
                    Text(text = "Watch Ad")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialogForProAd = false }
                ) {
                    Text(text = "Cancel")
                }
            }
        )
    }

    Scaffold(
        topAppBar = {
            TopBar(
                leading = {
                    BackButton(
                        text = "Quick Mode",
                        onClick = navController::navigateUp
                    )
                },
                trailing = {
                    AnimatedVisibility(
                        visible = selectionState == SelectionState.COMPLETED
                    ) {
                        IconButton(onClick = { selectionState = SelectionState.RUNNING }) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Shuffle")
                        }
                    }
                }
            )
        }
    ) {

        val alpha by animateFloatAsState(
            targetValue = when (selectionState) {
                SelectionState.RUNNING -> 1f
                SelectionState.COMPLETED -> 0f
            }
        )

        LaunchedEffect(selectionState) {
            if (selectionState == SelectionState.COMPLETED) return@LaunchedEffect
            delay(250)
            val repeatCount = Random.nextInt(5, 10)
            repeat(repeatCount) {
                lookingIndex = lookingIndex.inc().mod(categories.size)
                delay(300)
            }
            selectionState = SelectionState.COMPLETED
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                SlideOnChange(
                    targetState = lookingIndex,
                    direction = SlideDirection.Up
                ) { index ->
                    Text(
                        modifier = Modifier.clickable(
                            enabled = selectionState == SelectionState.COMPLETED,
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            if (!canStartGame) {
                                showDialogForEnergyAd = true
                                return@clickable
                            }
                            if (categories[lookingIndex].forPro) {
                                showDialogForProAd = true
                                return@clickable
                            }
                            navController.navigate(AppScreen.COUNT_DOWN.route.format(lookingIndex)) {
                                launchSingleTop = true
                            }
                        },
                        text = categories[index].emoji,
                        fontSize = 96.sp,
                    )
                }

                SelectedCategoryDetail(
                    index = lookingIndex,
                    visible = selectionState == SelectionState.COMPLETED
                )

            }

            Box {
                Text(
                    modifier = Modifier
                        .padding(24.dp)
                        .alpha(alpha),
                    text = "We are choosing a category for you.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SelectedCategoryDetail(
    index: Int,
    visible: Boolean
) {
    val alpha by animateFloatAsState(
        targetValue = when (visible) {
            true -> 1f
            else -> 0f
        }
    )

    Column(
        modifier = Modifier.alpha(alpha),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        with(categories[index]) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = name, style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Click on the emoji to start the game",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )

            if (!forPro) return@with

            Spacer(modifier = Modifier.height(4.dp))
            Badge(modifier = Modifier.padding(8.dp)) {
                Text(text = "PRO", fontSize = 14.sp)
            }
        }
    }
}


