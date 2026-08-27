package com.revolutionary.codelearn.ui.screens.ide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.ui.components.CodePlaygroundArea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(
    onClose: () -> Unit,
    viewModel: IdeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("IDE") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Language.entries.forEach { language ->
                    FilterChip(
                        selected = uiState.language == language,
                        onClick = { viewModel.selectLanguage(language) },
                        label = { Text(language.displayName) },
                    )
                }
            }
            CodePlaygroundArea(
                language = uiState.language,
                code = uiState.code,
                onCodeChange = viewModel::onCodeChange,
                isRunning = uiState.isRunning,
                result = uiState.result,
                onRun = viewModel::runCode,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
