package quiz.game.trivia.presentation.screens.connect_play

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ConnectGPlayScreen(
    navController: NavController,
    isGooglePlayConnected: Boolean,
    isConnecting: Boolean,
    connectToGooglePlayGames: () -> Unit,
) {

    val context = LocalContext.current
    val connectToGooglePlayGamesFunc by rememberUpdatedState(newValue = connectToGooglePlayGames)

    var showCloseButton by rememberSaveable {
        mutableStateOf(false)
    }

    if (isGooglePlayConnected) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        showCloseButton = true
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isConnecting) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(150.dp)
                    .padding(16.dp)
                    .align(Alignment.Center)
            )
        } else {
            AnimatedVisibility(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd),
                visible = showCloseButton
            ) {
                IconButton(onClick = navController::popBackStack) {
                    Icon(imageVector = Icons.Rounded.Close, contentDescription = null)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(56.dp),
                        imageVector = Icons.Rounded.SportsEsports,
                        contentDescription = null,
                        tint = contentColorFor(
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Sync progress",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier=Modifier.widthIn(max = 400.dp),
                    text = "In order to synchronize your progress and take part in the leaderboard, it is necessary for you to connect with Google Play Games.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(36.dp))
                Button(onClick = connectToGooglePlayGamesFunc) {
                    Text(text = "Sign in with Google Play Games")
                }
            }
        }
    }

    BackHandler {
        if (isConnecting) return@BackHandler
    }

}