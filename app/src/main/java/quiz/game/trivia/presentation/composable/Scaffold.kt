package quiz.game.trivia.presentation.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Scaffold(
    topAppBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        topAppBar()
        Spacer(modifier = Modifier.height(16.dp))
        content()
    }
}