package com.revolutionary.codelearn.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.unit.dp
import com.revolutionary.codelearn.ui.components.renderInlineCode

@Composable
fun QuizScreen(
    languageId: String,
    trackId: String,
    quizId: String,
    onFinish: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(languageId, trackId, quizId) {
        viewModel.load(languageId, trackId, quizId)
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onFinish) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
                LinearProgressIndicator(
                    progress = { if (uiState.selectedIndex != null) 1f else 0f },
                    modifier = Modifier.weight(1f).height(8.dp).padding(end = 16.dp),
                )
            }
        },
    ) { padding ->
        val quiz = uiState.quiz
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading || quiz == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                    Text(text = renderInlineCode(quiz.prompt))

                    Column(
                        modifier = Modifier.weight(1f).padding(top = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        quiz.choices.forEachIndexed { index, choice ->
                            ChoiceRow(
                                text = choice,
                                state = choiceState(uiState.selectedIndex, index, quiz.correctIndex),
                                onClick = { viewModel.selectChoice(index) },
                            )
                        }
                        if (uiState.selectedIndex != null) {
                            Text(
                                text = quiz.explanation,
                                modifier = Modifier.padding(top = 12.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.finish()
                            onFinish()
                        },
                        enabled = uiState.selectedIndex != null,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}

private enum class ChoiceState { UNSELECTED, CORRECT, INCORRECT, REVEALED_CORRECT }

private fun choiceState(selectedIndex: Int?, index: Int, correctIndex: Int): ChoiceState = when {
    selectedIndex == null -> ChoiceState.UNSELECTED
    index == selectedIndex && index == correctIndex -> ChoiceState.CORRECT
    index == selectedIndex -> ChoiceState.INCORRECT
    index == correctIndex -> ChoiceState.REVEALED_CORRECT
    else -> ChoiceState.UNSELECTED
}

@Composable
private fun ChoiceRow(text: String, state: ChoiceState, onClick: () -> Unit) {
    val borderColor = when (state) {
        ChoiceState.CORRECT, ChoiceState.REVEALED_CORRECT -> Color(0xFF4CAF50)
        ChoiceState.INCORRECT -> Color(0xFFE53935)
        ChoiceState.UNSELECTED -> MaterialTheme.colorScheme.outline
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(borderColor.copy(alpha = if (state == ChoiceState.UNSELECTED) 0f else 0.12f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Text(text = renderInlineCode(text))
    }
}
