package quiz.game.trivia.presentation.screens.choose_mode

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import quiz.game.trivia.domain.models.GameMode
import quiz.game.trivia.domain.models.gameModes
import quiz.game.trivia.presentation.composable.BackButton
import quiz.game.trivia.presentation.composable.Scaffold
import quiz.game.trivia.presentation.composable.TopBar

@Composable
fun ChooseModeScreen(navController: NavController) {

    Scaffold(
        topAppBar = {
            TopBar(leading = {
                BackButton(
                    text = "Choose Mode",
                    onClick = navController::navigateUp
                )
            })
        }
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            gameModes.forEach { item ->
                GameMode(
                    mode = item,
                    onClick = {
                        navController.navigate(item.route.destination) {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GameMode(
    mode: GameMode,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(4)
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(shape)
            .wrapContentHeight()
            .fillMaxWidth()
            .border(
                1.dp,
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape
            ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = mode.emoji, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.weight(1f),
                    text = mode.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 10.dp),
                text = mode.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}