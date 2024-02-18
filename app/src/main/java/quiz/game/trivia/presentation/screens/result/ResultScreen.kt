package quiz.game.trivia.presentation.screens.result

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import quiz.game.trivia.R
import quiz.game.trivia.presentation.ads.showInterstitial
import quiz.game.trivia.presentation.navigation.AppScreen
import quiz.game.trivia.presentation.util.findActivity
import java.util.concurrent.TimeUnit

@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: ResultScreenViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val isConnectedWithGooglePlayGames by viewModel.isConnectedWithGooglePlayGames.collectAsState()
    val context = LocalContext.current

    var isGooglePlayGamesWorkDone by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.calculatePoints()
    }

    DisposableEffect(isConnectedWithGooglePlayGames) {

        if (isConnectedWithGooglePlayGames && !isGooglePlayGamesWorkDone) {
            context.findActivity()?.run {
                val achievementsClient = PlayGames.getAchievementsClient(this)
                val points = viewModel.getPoints()

                if (points > 0) {
                    PlayGames.getLeaderboardsClient(this)
                        .loadCurrentPlayerLeaderboardScore(
                            context.getString(R.string.leaderboard_id),
                            LeaderboardVariant.TIME_SPAN_ALL_TIME,
                            LeaderboardVariant.COLLECTION_PUBLIC
                        )
                        .addOnSuccessListener { score ->
                            val currentScore = score.get()?.rawScore ?: 0
                            achievementsClient.unlock(getString(R.string.achievement_first_game))
                            achievementsClient.increment(
                                getString(R.string.achievement_tenth_game),
                                1
                            )
                            achievementsClient.increment(
                                getString(R.string.achievement_fifty_game),
                                1
                            )
                            achievementsClient.increment(
                                getString(R.string.achievement_hundred_game),
                                1
                            )

                            val leaderboardsClient = PlayGames.getLeaderboardsClient(this)
                            leaderboardsClient.submitScore(
                                getString(R.string.leaderboard_id),
                                currentScore + points
                            )

                            val streakCount = viewModel.getStreakCount().toInt()
                            if (streakCount > 0) {
                                achievementsClient.unlock(getString(R.string.achievement_first_streak))
                                achievementsClient.increment(
                                    getString(R.string.achievement_streak_lover),
                                    streakCount
                                )
                                achievementsClient.increment(
                                    getString(R.string.achievement_streak_mastery),
                                    streakCount
                                )
                                achievementsClient.increment(
                                    getString(R.string.achievement_streak_freak),
                                    streakCount
                                )
                            }
                        }
                }

                isGooglePlayGamesWorkDone = true
            }
        }

        onDispose {
            isGooglePlayGamesWorkDone = false
        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .clickable(
                    enabled = !uiState.canShowStats,
                    onClick = viewModel::makeStatsVisible,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
        ) {
            KonfettiView(
                modifier = Modifier.fillMaxSize(),
                parties = listOf(
                    Party(
                        emitter = Emitter(
                            duration = 3,
                            TimeUnit.SECONDS
                        ).perSecond(50),
                        position = Position.Relative(0.0, 0.0)
                    ),
                    Party(
                        emitter = Emitter(
                            duration = 3,
                            TimeUnit.SECONDS
                        ).perSecond(50),
                        position = Position.Relative(1.0, 0.0)
                    )
                ),
                updateListener = object : OnParticleSystemUpdateListener {
                    override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                        viewModel.makeStatsVisible()
                    }
                }
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🥳", fontSize = 64.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Congratulations!!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                AnimatedVisibility(visible = uiState.canShowStats, enter = fadeIn()) {
                    val status = if (isConnectedWithGooglePlayGames) {
                        "You get +${uiState.points} Trivia Points"
                    } else {
                        "Connect to Google Play Games for earning points"
                    }
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        HorizontalDivider()
        AnimatedVisibility(
            visible = uiState.canShowStats,
            enter = expandVertically() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }) + fadeIn()
        ) {
            Column {
                LazyVerticalGrid(
                    modifier = Modifier.padding(bottom = 32.dp, top = 24.dp),
                    columns = GridCells.Fixed(2)
                ) {
                    val stats = mapOf(
                        "STREAK" to uiState.streakCount,
                        "CORRECT" to uiState.correctCount,
                        "INCORRECT" to uiState.incorrectCount,
                        "MISSED" to uiState.missedCount,
                    )
                    items(stats.toList()) { (title, value) ->
                        GameStat(title, value)
                    }
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .padding(horizontal = 32.dp),
                    onClick = {
                        showInterstitial(context) {
                            navController.navigate(AppScreen.HOME.route.format(true, true)) {
                                launchSingleTop = true
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                ) {
                    Text(text = "CONTINUE", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }

    BackHandler {
        if (!uiState.canShowStats) return@BackHandler
        showInterstitial(context) {
            navController.navigate(AppScreen.HOME.route.format(true, true)) {
                launchSingleTop = true
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

}


@Composable
private fun GameStat(title: String, value: Long) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.secondary
        )

    }
}
