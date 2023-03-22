package quiz.game.trivia.presentation.screens.home

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import kotlinx.coroutines.delay
import quiz.game.trivia.R
import quiz.game.trivia.domain.INTERVAL_MINUTES
import quiz.game.trivia.domain.models.GameData
import quiz.game.trivia.domain.models.UserData
import quiz.game.trivia.presentation.navigation.AppScreen
import quiz.game.trivia.presentation.util.findActivity


enum class ButtonState {
    EXPANDED, COLLAPSED
}

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    userData: UserData,
    askReview: () -> Unit
) {

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val askReviewFunc by rememberUpdatedState(newValue = askReview)

    if (uiState.shouldAskReview) {
        LaunchedEffect(Unit) {
            askReviewFunc()
            viewModel.onReviewAsked()
        }
    }

    var gameData by rememberSaveable {
        mutableStateOf(GameData())
    }

    if (uiState.shouldRefreshScore) {
        LaunchedEffect(Unit) {
            context.findActivity()?.run {
                PlayGames.getLeaderboardsClient(this)
                    .loadCurrentPlayerLeaderboardScore(
                        getString(R.string.leaderboard_id),
                        LeaderboardVariant.TIME_SPAN_ALL_TIME,
                        LeaderboardVariant.COLLECTION_PUBLIC
                    )
                    .addOnSuccessListener { score ->
                        val rank = score.get()?.displayRank.orEmpty()
                        val points = score.get()?.displayScore
                            ?.replace("points", "")
                            .orEmpty()
                        gameData = gameData.copy(rank = rank, points = points)
                    }
                viewModel.onScoreRefreshed()
            }
        }
    }

    var buttonState by remember {
        mutableStateOf(ButtonState.COLLAPSED)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.TopEnd),
            onClick = { navController.navigate(AppScreen.SETTINGS.destination) },
        ) {
            Icon(imageVector = Icons.Rounded.Settings, contentDescription = null)
        }

        UserDetails(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 16.dp),
            energy = uiState.energy,
            userData = userData,
            gameData = gameData
        )
        AnimatedContainer(
            modifier = Modifier.align(Alignment.BottomCenter),
            targetState = buttonState,
            onExpandAction = { buttonState = ButtonState.EXPANDED },
            collapsedContent = { PlayNowButton() },
            expandedContent = {
                Surface {
                    LaunchedEffect(Unit) {
                        delay(250)
                        navController.navigate(AppScreen.CHOOSE_MODE.destination) {
                            launchSingleTop = true
                        }
                        delay(250)
                        buttonState=ButtonState.COLLAPSED
                    }
                }
            },
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun UserDetails(
    modifier: Modifier = Modifier,
    userData: UserData,
    gameData: GameData,
    energy: Int?,
) {

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {}
    )

    var showEnergyInfoDialog by remember {
        mutableStateOf(false)
    }

    var showPointsInfoDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    if (showEnergyInfoDialog) {
        AlertDialog(
            onDismissRequest = { showEnergyInfoDialog = false },
            icon = { Icon(imageVector = Icons.Rounded.ElectricBolt, contentDescription = null) },
            title = { Text(text = "Energy") },
            text = { Text(text = "In order to initiate a game, you need to have an energy supply, which will replenish automatically every $INTERVAL_MINUTES minutes.") },
            confirmButton = {
                TextButton(
                    onClick = { showEnergyInfoDialog = false }
                ) {
                    Text(text = "Got it")
                }
            },
        )
    }

    if (showPointsInfoDialog) {
        AlertDialog(
            onDismissRequest = { showPointsInfoDialog = false },
            icon = { Icon(imageVector = Icons.Rounded.StarHalf, contentDescription = null) },
            title = { Text(text = "Points") },
            text = { Text(text = "Your rank is determined by the points you earn from answering questions correctly, and the difficulty of the question corresponds to a specific number of points.\n\nEasy questions earn 2x points\nMedium questions earn 3x points\nHard questions earn 4x points\n\nA streak of correct answers can also boost your points and rank.") },
            confirmButton = {
                TextButton(
                    onClick = { showPointsInfoDialog = false }
                ) {
                    Text(text = "Got it")
                }
            },
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello,",
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = userData.name.plus("!"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = !userData.profilePic?.toString().isNullOrBlank()
            ) { profilePicAvailable ->
                if (profilePicAvailable) {
                    var drawable by remember {
                        mutableStateOf<Drawable?>(null)
                    }

                    LaunchedEffect(userData.profilePic) {
                        ImageManager.create(context).loadImage(
                            { _, p1, _ -> drawable = p1 },
                            userData.profilePic!!
                        )
                    }

                    AsyncImage(
                        model = drawable,
                        contentDescription = null
                    )
                } else {
                    Icon(
                        modifier = Modifier.size(42.dp),
                        imageVector = Icons.Rounded.Person4,
                        contentDescription = null,
                        tint = contentColorFor(backgroundColor = MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(),
            ) {
                UserPointsDetailItem(
                    title = "RANK",
                    value = gameData.rank.orEmpty().ifBlank { "--" },
                    shape = RoundedCornerShape(topStartPercent = 16),
                    paddingValues = PaddingValues(end = 1.dp),
                    onClick = {
                        context.findActivity()?.let { activity ->
                            PlayGames.getLeaderboardsClient(activity)
                                .getLeaderboardIntent(context.getString(R.string.leaderboard_id))
                                .addOnSuccessListener(activityLauncher::launch)
                        }
                    }
                )
                UserPointsDetailItem(
                    title = "POINTS",
                    value = gameData.points.orEmpty().ifBlank { "--" },
                    shape = RoundedCornerShape(topEndPercent = 16),
                    paddingValues = PaddingValues(start = 1.dp),
                    onClick = { showPointsInfoDialog = true }
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(),
            ) {
                UserPointsDetailItem(
                    title = "ENERGY",
                    value = energy?.toString().orEmpty().ifBlank { "--" },
                    shape = RoundedCornerShape(bottomStartPercent = 16),
                    paddingValues = PaddingValues(start = 1.dp),
                    onClick = { showEnergyInfoDialog = true }
                )
                UserPointsDetailItem(
                    title = "ACHIEVEMENTS",
                    icon = {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Rounded.EmojiEvents,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    },
                    shape = RoundedCornerShape(bottomEndPercent = 16),
                    paddingValues = PaddingValues(end = 1.dp),
                    onClick = {
                        context.findActivity()?.let { activity ->
                            PlayGames.getAchievementsClient(activity)
                                .achievementsIntent
                                .addOnSuccessListener(activityLauncher::launch)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RowScope.UserPointsDetailItem(
    shape: Shape = RectangleShape,
    paddingValues: PaddingValues,
    title: String,
    value: String? = null,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .padding(paddingValues)
            .height(100.dp),
        shape = shape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            value?.let {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }
            icon?.invoke()
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 2.sp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun AnimatedContainer(
    modifier: Modifier = Modifier,
    targetState: ButtonState,
    expandedContent: @Composable () -> Unit,
    collapsedContent: @Composable () -> Unit,
    onExpandAction: () -> Unit,
) {
    val transition = updateTransition(
        targetState,
        label = "Play Now Button Transition"
    )

    val padding by transition.animateDp(label = "Play Now Button Padding") { state ->
        when (state) {
            ButtonState.COLLAPSED -> 24.dp
            ButtonState.EXPANDED -> 0.dp
        }
    }

    val height by transition.animateDp(
        label = "Play Now Button Height",
        transitionSpec = { if (initialState == ButtonState.COLLAPSED) tween(400) else tween(350) }) { state ->
        when (state) {
            ButtonState.COLLAPSED -> 100.dp
            ButtonState.EXPANDED -> LocalConfiguration.current.screenHeightDp.dp
        }
    }

    val maxWidth by transition.animateDp(label = "Play Now Button MaxWidth") { state ->
        when (state) {
            ButtonState.COLLAPSED -> 500.dp
            ButtonState.EXPANDED -> Dp.Unspecified
        }
    }

    val corners by transition.animateFloat(label = "Play Now Button Corners") { state ->
        when (state) {
            ButtonState.COLLAPSED -> 100f
            ButtonState.EXPANDED -> 0f
        }
    }

    val color by transition.animateColor(
        label = "Play Now Button Background Color",
        transitionSpec = {
            if (initialState == ButtonState.EXPANDED) tween(delayMillis = 100) else spring()
        }) { state ->
        when (state) {
            ButtonState.COLLAPSED -> MaterialTheme.colorScheme.primary
            ButtonState.EXPANDED -> MaterialTheme.colorScheme.background
        }
    }

    Surface(
        modifier = modifier
            .widthIn(max = maxWidth)
            .fillMaxWidth()
            .height(height)
            .navigationBarsPadding()
            .padding(padding)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = if (targetState == ButtonState.COLLAPSED) rememberRipple() else null,
                onClick = onExpandAction
            ),
        color = color,
        shape = RoundedCornerShape(corners),
    ) {
        AnimatedVisibility(
            visible = targetState == ButtonState.COLLAPSED,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 100,
                )
            ),
            exit = ExitTransition.None
        ) {
            collapsedContent()
        }
        AnimatedVisibility(
            visible = targetState == ButtonState.EXPANDED,
            enter = fadeIn(
                animationSpec = tween(
                    delayMillis = 200,
                )
            ),
            exit = ExitTransition.None
        ) {
            expandedContent()
        }
    }

}


@Composable
private fun PlayNowButton() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            4.dp,
            Alignment.CenterHorizontally
        ),
    ) {
        Icon(imageVector = Icons.Filled.SportsEsports, contentDescription = "Play Game")
        Text(text = "PLAY NOW", style = MaterialTheme.typography.labelLarge)
    }
}
