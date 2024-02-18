package quiz.game.trivia.presentation.screens.casual_mode

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import quiz.game.trivia.domain.models.Category
import quiz.game.trivia.domain.models.categories
import quiz.game.trivia.presentation.ads.showEnergyRewardAd
import quiz.game.trivia.presentation.ads.showRewardAd
import quiz.game.trivia.presentation.anim.SlideDirection
import quiz.game.trivia.presentation.anim.SlideOnChange
import quiz.game.trivia.presentation.composable.BackButton
import quiz.game.trivia.presentation.composable.Scaffold
import quiz.game.trivia.presentation.composable.TopBar
import quiz.game.trivia.presentation.navigation.AppScreen

@Composable
fun CasualModeScreen(
    navController: NavController,
    viewModel: CasualModeScreenViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val canStartGame by viewModel.isEnergySufficient.collectAsState()

    var selectedIndex by rememberSaveable {
        mutableStateOf(0)
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
                                if (categories[selectedIndex].forPro) {
                                    showDialogForProAd = true
                                    return@showEnergyRewardAd
                                }
                                navController.navigate(
                                    route = AppScreen.COUNT_DOWN.route.format(selectedIndex)
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
                            navController.navigate(AppScreen.COUNT_DOWN.route.format(selectedIndex)) {
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
            TopBar(leading = {
                BackButton(
                    text = "Casual Mode",
                    onClick = navController::navigateUp
                )
            })
        }
    ) {
        Column {
            SelectedCategoryDetail(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                selectedIndex = selectedIndex,
            )
            CategoryList(
                selectedIndex = selectedIndex,
                onItemClick = {
                    if (selectedIndex != it) {
                        selectedIndex = it
                        return@CategoryList
                    }
                    if (!canStartGame) {
                        showDialogForEnergyAd = true
                        return@CategoryList
                    }
                    if (categories[selectedIndex].forPro) {
                        showDialogForProAd = true
                        return@CategoryList
                    }
                    navController.navigate(AppScreen.COUNT_DOWN.route.format(selectedIndex)) {
                        launchSingleTop = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

    }
}

@Composable
private fun CategoryList(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    LazyRow(
        modifier = modifier
            .padding(vertical = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        itemsIndexed(categories) { index, item ->
            Spacer(modifier = Modifier.width(if (index == 0) 24.dp else 4.dp))
            CategoryItem(
                category = item,
                selected = selectedIndex == index,
                onClick = { onItemClick(index) }
            )
            Spacer(modifier = Modifier.width(if (index == categories.lastIndex) 24.dp else 4.dp))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun SelectedCategoryDetail(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
) {

    val alpha by animateFloatAsState(
        targetValue = if (categories[selectedIndex].forPro) 1f else 0f
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SlideOnChange(
            targetState = selectedIndex,
            direction = SlideDirection.Adaptive
        ) { index ->
            Text(
                text = categories[index].emoji,
                fontSize = 80.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AnimatedContent(
            targetState = selectedIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() }
        ) { index ->
            Text(
                text = categories[index].name,
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Text(
            text = "Click again to start the game",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        Badge(
            modifier = Modifier
                .padding(8.dp)
                .alpha(alpha)
        ) {
            Text(text = "PRO", fontSize = 14.sp)
        }
    }
}

@Composable
private fun CategoryItem(
    modifier: Modifier = Modifier,
    category: Category,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .size(60.dp)
            .clickable(
                onClick = onClick,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() }
            )
            .border(
                if (selected) 0.dp else 2.dp,
                MaterialTheme.colorScheme.secondaryContainer,
                CircleShape
            )
            .background(
                if (selected) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.background
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = category.emoji, style = MaterialTheme.typography.titleLarge)
    }
}
