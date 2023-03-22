package quiz.game.trivia.presentation.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment


@Composable
fun BackButton(text: String, onClick: () -> Unit) {
    FilledTonalButton(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Filled.ChevronLeft, contentDescription = "Go back")
            Text(text = text.uppercase())
        }
    }
}