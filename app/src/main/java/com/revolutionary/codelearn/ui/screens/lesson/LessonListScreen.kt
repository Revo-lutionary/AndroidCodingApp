package com.revolutionary.codelearn.ui.screens.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
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
import com.revolutionary.codelearn.core.model.Track

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonListScreen(
    languageId: String,
    onLessonSelected: (trackId: String, lessonId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: LessonListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(languageId) {
        viewModel.load(languageId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(languageId) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.tracks) { track ->
                        TrackSection(track = track, onLessonSelected = onLessonSelected)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackSection(
    track: Track,
    onLessonSelected: (trackId: String, lessonId: String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = track.title)
        track.lessonIds.forEach { lessonId ->
            Card(
                onClick = { onLessonSelected(track.id, lessonId) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = lessonId, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
