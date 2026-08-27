package com.revolutionary.codelearn.ui.screens.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.editor.CodeEditorField
import com.revolutionary.codelearn.core.editor.insertAtCursor
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.ui.components.ExplainWithAiButton
import com.revolutionary.codelearn.ui.components.SymbolToolbar
import com.revolutionary.codelearn.ui.components.renderInlineCode
import io.github.rosemoe.sora.widget.CodeEditor

private val TAB_TITLES = listOf("Reference", "Challenge", "Code", "Solution")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LessonScreen(
    languageId: String,
    trackId: String,
    lessonId: String,
    onFinish: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val isLastTab = selectedTab == TAB_TITLES.lastIndex
    // Hide the persistent Continue bar while the keyboard is up: it would sit
    // behind the IME anyway, and its reserved height would otherwise stack
    // with imePadding() below and push the Code tab's toolbar/Run button up
    // far above the real keyboard.
    val imeVisible = WindowInsets.isImeVisible

    LaunchedEffect(languageId, trackId, lessonId) {
        viewModel.load(languageId, trackId, lessonId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(uiState.lesson?.title ?: "") },
                    navigationIcon = {
                        IconButton(onClick = onFinish) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                )
                if (uiState.lesson != null) {
                    ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 12.dp) {
                        TAB_TITLES.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title, maxLines = 1) },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (uiState.lesson != null && !imeVisible) {
                Surface(shadowElevation = 4.dp) {
                    Button(
                        onClick = {
                            if (isLastTab) {
                                viewModel.markComplete()
                                onFinish()
                            } else {
                                selectedTab++
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    ) {
                        Text(if (isLastTab) "Finish" else "Continue")
                    }
                }
            }
        },
    ) { padding ->
        val lesson = uiState.lesson
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading || lesson == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (selectedTab) {
                    0 -> ReferenceTab(lesson.referenceMarkdown)
                    1 -> ChallengeTab(lesson.challengeMarkdown, uiState.code)
                    2 -> CodeTab(
                        language = lesson.language,
                        uiState = uiState,
                        onCodeChange = viewModel::onCodeChange,
                        onRun = viewModel::runCode,
                    )
                    3 -> SolutionTab(lesson.solutionCode)
                }
            }
        }
    }
}

@Composable
private fun ReferenceTab(referenceMarkdown: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(text = renderInlineCode(referenceMarkdown))
    }
}

@Composable
private fun ChallengeTab(challengeMarkdown: String, currentCode: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = renderInlineCode(challengeMarkdown))
        ExplainWithAiButton {
            "I'm learning to code and stuck on this challenge:\n\n$challengeMarkdown\n\nHere's what I have so far:\n\n$currentCode\n\nCan you explain the concept and give me a hint (not the full answer)?"
        }
    }
}

@Composable
private fun CodeTab(
    language: Language,
    uiState: LessonUiState,
    onCodeChange: (String) -> Unit,
    onRun: () -> Unit,
) {
    var editorRef by remember { mutableStateOf<CodeEditor?>(null) }

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        CodeEditorField(
            code = uiState.code,
            onCodeChange = onCodeChange,
            language = language,
            modifier = Modifier.fillMaxWidth().weight(1f),
            onEditorReady = { editorRef = it },
        )

        val result = uiState.result
        if (result != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
            ) {
                if (result.stdout.isNotBlank()) {
                    Text(text = result.stdout, fontFamily = FontFamily.Monospace)
                }
                if (result.stderr.isNotBlank()) {
                    Text(
                        text = result.stderr,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }

        SymbolToolbar(
            language = language,
            onSymbolTapped = { symbol -> editorRef?.insertAtCursor(symbol) },
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(onClick = onRun, enabled = !uiState.isRunning) {
                if (uiState.isRunning) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text(" Run")
                }
            }
        }
    }
}

@Composable
private fun SolutionTab(solutionCode: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = solutionCode,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
