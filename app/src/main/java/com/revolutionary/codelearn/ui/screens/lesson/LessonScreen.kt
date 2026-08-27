package com.revolutionary.codelearn.ui.screens.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.ui.components.CodePlaygroundArea
import com.revolutionary.codelearn.ui.components.renderInlineCode

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
                    TabRow(selectedTabIndex = selectedTab) {
                        TAB_TITLES.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        text = title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        fontSize = 12.sp,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (uiState.lesson != null && !imeVisible) {
                Surface(shadowElevation = 4.dp, modifier = Modifier.navigationBarsPadding()) {
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
        // Bottom inset is handled per-tab below (nav bar for the scrollable
        // tabs, nav-bar-or-keyboard for the Code tab) rather than reserved
        // here, since Scaffold's own padding always includes the nav bar
        // unconditionally -- stacking that with a tab's own keyboard-aware
        // padding is what caused the gap above the keyboard.
    ) { padding ->
        val lesson = uiState.lesson
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
            if (uiState.isLoading || lesson == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                when (selectedTab) {
                    0 -> ReferenceTab(lesson.referenceMarkdown)
                    1 -> ChallengeTab(lesson.challengeMarkdown)
                    2 -> CodePlaygroundArea(
                        language = lesson.language,
                        code = uiState.code,
                        onCodeChange = viewModel::onCodeChange,
                        isRunning = uiState.isRunning,
                        result = uiState.result,
                        onRun = viewModel::runCode,
                        modifier = Modifier.fillMaxSize(),
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
            .navigationBarsPadding()
            .padding(20.dp),
    ) {
        Text(text = renderInlineCode(referenceMarkdown))
    }
}

@Composable
private fun ChallengeTab(challengeMarkdown: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(text = renderInlineCode(challengeMarkdown))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "💡 Stuck? Hold your phone's power button to ask Gemini for help.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SolutionTab(solutionCode: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
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
