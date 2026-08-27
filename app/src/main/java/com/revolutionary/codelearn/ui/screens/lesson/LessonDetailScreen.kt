package com.revolutionary.codelearn.ui.screens.lesson

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.editor.CodeEditorField
import com.revolutionary.codelearn.core.model.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    languageId: String,
    trackId: String,
    lessonId: String,
    onBack: () -> Unit,
    viewModel: LessonDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(languageId, trackId, lessonId) {
        viewModel.load(languageId, trackId, lessonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.lesson?.title ?: lessonId) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        val lesson = uiState.lesson
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading || lesson == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Text(text = lesson.explanationMarkdown)
                    CodeEditorField(
                        code = uiState.code,
                        onCodeChange = viewModel::onCodeChange,
                        language = Language.fromId(languageId),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .padding(top = 16.dp),
                    )
                }
            }
        }
    }
}
