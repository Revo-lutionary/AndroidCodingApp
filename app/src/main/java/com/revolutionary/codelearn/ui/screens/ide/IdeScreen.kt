package com.revolutionary.codelearn.ui.screens.ide

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.data.IdeTabEntity
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.ui.components.CodePlaygroundArea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(
    onClose: () -> Unit,
    viewModel: IdeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab = uiState.tabs.firstOrNull { it.id == uiState.selectedTabId }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("IDE") },
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                )
                TabStrip(
                    tabs = uiState.tabs,
                    selectedId = uiState.selectedTabId,
                    onSelect = viewModel::selectTab,
                    onCloseTab = viewModel::closeTab,
                    onAddTab = viewModel::addTab,
                )
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (selectedTab == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Language.entries.forEach { language ->
                            FilterChip(
                                selected = selectedTab.languageId == language.id,
                                onClick = { viewModel.selectLanguage(selectedTab.id, language) },
                                label = { Text(language.displayName) },
                            )
                        }
                    }
                    CodePlaygroundArea(
                        language = Language.fromId(selectedTab.languageId),
                        code = selectedTab.code,
                        onCodeChange = { viewModel.onCodeChange(selectedTab.id, it) },
                        isRunning = uiState.isRunning,
                        result = uiState.results[selectedTab.id],
                        onRun = { viewModel.runCode(selectedTab.id) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun TabStrip(
    tabs: List<IdeTabEntity>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onAddTab: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            TabChip(
                tab = tab,
                selected = tab.id == selectedId,
                onClick = { onSelect(tab.id) },
                onClose = if (tabs.size > 1) ({ onCloseTab(tab.id) }) else null,
            )
        }
        IconButton(onClick = onAddTab) {
            Icon(Icons.Default.Add, contentDescription = "New tab")
        }
    }
}

@Composable
private fun TabChip(
    tab: IdeTabEntity,
    selected: Boolean,
    onClick: () -> Unit,
    onClose: (() -> Unit)?,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, end = if (onClose != null) 4.dp else 14.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = tab.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.widthIn(max = 96.dp),
            )
            if (onClose != null) {
                IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close tab", modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
