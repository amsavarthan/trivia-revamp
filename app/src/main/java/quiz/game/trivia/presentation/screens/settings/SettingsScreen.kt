package quiz.game.trivia.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import quiz.game.trivia.domain.models.Difficulty
import quiz.game.trivia.domain.models.QuestionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    connectToGooglePlayGames: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    val connectToGooglePlayGamesFunc by rememberUpdatedState(newValue = connectToGooglePlayGames)

    if (uiState.isChoosingDifficulty) {
        AlertDialog(
            onDismissRequest = viewModel::toggleIsDifficultyChoosing,
            title = { Text(text = "Difficulty") },
            text = {
                Column {
                    Text(text = "Choose difficulty from the options below.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(Modifier.selectableGroup()) {
                        Difficulty.entries.forEach {
                            DialogChooserRow(
                                text = it.toString(),
                                selected = uiState.gameConfig.difficulty == it,
                                onClick = {
                                    viewModel.updateDifficulty(it)
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::toggleIsDifficultyChoosing) {
                    Text(text = "Done")
                }
            }
        )
    }

    if (uiState.isChoosingQuestionType) {
        AlertDialog(
            onDismissRequest = viewModel::toggleIsQuestionTypeChoosing,
            title = { Text(text = "Question Type") },
            text = {
                Column {
                    Text(text = "Choose question type from the options below.")
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(Modifier.selectableGroup()) {
                        QuestionType.entries.forEach {
                            DialogChooserRow(
                                text = it.toString(),
                                selected = uiState.gameConfig.type == it,
                                onClick = {
                                    viewModel.updateQuestionType(it)
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::toggleIsQuestionTypeChoosing) {
                    Text(text = "Done")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = navController::navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                if (uiState.isConnectedToGooglePlayGames) return@item
                ListItem(
                    modifier = Modifier.clickable(onClick = connectToGooglePlayGamesFunc),
                    headlineContent = {
                        Text(text = "Disconnected from Google Play")
                    },
                    supportingContent = {
                        Text(text = "Click to connect")
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.SportsEsports,
                                contentDescription = null,
                                tint = contentColorFor(
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    text = "Game Configuration".uppercase(),
                    style = MaterialTheme.typography.labelSmall
                )
                val difficulty = uiState.gameConfig.difficulty
                ListItem(
                    modifier = Modifier.clickable(onClick = viewModel::toggleIsDifficultyChoosing),
                    headlineContent = {
                        Text(text = "Difficulty")
                    },
                    supportingContent = {
                        Text(text = difficulty.toString())
                    },
                    trailingContent = {
                        Text(text = difficulty.emoji, fontSize = 24.sp)
                    }
                )
            }
            item {
                val questionType = uiState.gameConfig.type
                ListItem(
                    modifier = Modifier.clickable(onClick = viewModel::toggleIsQuestionTypeChoosing),
                    headlineContent = {
                        Text(text = "Questions Type")
                    },
                    supportingContent = {
                        Text(text = questionType.toString())
                    },
                    trailingContent = {
                        Text(text = questionType.emoji, fontSize = 24.sp)
                    }
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp, bottom = 12.dp),
                    text = "Notifications".uppercase(),
                    style = MaterialTheme.typography.labelSmall
                )
                ListItem(
                    modifier = Modifier.clickable(onClick = {
                        viewModel.updateEnergyAlertEnabledStatus(
                            !uiState.isEnergyAlertEnabled
                        )
                    }),
                    headlineContent = {
                        Text(text = "Energy Refill Alert")
                    },
                    trailingContent = {
                        Switch(
                            checked = uiState.isEnergyAlertEnabled,
                            onCheckedChange = viewModel::updateEnergyAlertEnabledStatus
                        )
                    }
                )
            }
        }
    }

}

@Composable
private fun DialogChooserRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
