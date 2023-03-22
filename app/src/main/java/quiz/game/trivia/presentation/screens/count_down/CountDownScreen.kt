package quiz.game.trivia.presentation.screens.count_down

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import quiz.game.trivia.presentation.anim.SlideDirection
import quiz.game.trivia.presentation.anim.SlideOnChange
import quiz.game.trivia.presentation.navigation.AppScreen

@Composable
fun CountDownScreen(
    navController: NavController,
    categoryIndex: Int
) {

    var count by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        //delayed for ux
        delay(900)
        while (count != 0) {
            count = count.dec()
            delay(1000)
        }
    }

    LaunchedEffect(count) {
        if (count == 0) {
            navController.navigate(AppScreen.GAME.route.format(categoryIndex)) {
                launchSingleTop = true
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }

    AnimatedVisibility(
        visible = count > 0,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CountDown(count)
        }
    }

    BackHandler(
        enabled = count > 0,
        onBack = navController::navigateUp
    )

}

@Composable
private fun CountDown(count: Int) {

    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        SlideOnChange(
            targetState = count,
            direction = SlideDirection.Down
        ) { targetCount ->
            Text(
                text = "$targetCount",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

}
