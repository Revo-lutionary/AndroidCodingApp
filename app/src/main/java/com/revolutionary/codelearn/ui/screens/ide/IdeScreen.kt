package com.revolutionary.codelearn.ui.screens.ide

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.data.IdeTabEntity
import com.revolutionary.codelearn.core.model.Language
import com.revolutionary.codelearn.ui.components.CodePlaygroundArea
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeScreen(
    onClose: () -> Unit,
    viewModel: IdeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab = uiState.tabs.firstOrNull { it.id == uiState.selectedTabId }
    var tabPendingClose by remember { mutableStateOf<IdeTabEntity?>(null) }
    var tabBeingRenamed by remember { mutableStateOf<IdeTabEntity?>(null) }

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
                    onCloseRequest = { tabPendingClose = it },
                    onRenameRequest = { tabBeingRenamed = it },
                    onReorder = viewModel::reorderTabs,
                    onAddTab = viewModel::addTab,
                )
            }
        },
    ) { padding ->
        // Bottom inset is handled inside CodePlaygroundArea itself (nav bar
        // or keyboard, whichever is taller right now); reserving it again
        // here would stack on top of that once the keyboard opens.
        Box(modifier = Modifier.fillMaxSize().padding(top = padding.calculateTopPadding())) {
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

    tabPendingClose?.let { tab ->
        AlertDialog(
            onDismissRequest = { tabPendingClose = null },
            title = { Text("Close \"${tab.title}\"?") },
            text = { Text("This tab's code will be deleted. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.closeTab(tab.id)
                    tabPendingClose = null
                }) { Text("Close tab") }
            },
            dismissButton = {
                TextButton(onClick = { tabPendingClose = null }) { Text("Cancel") }
            },
        )
    }

    tabBeingRenamed?.let { tab ->
        var text by remember(tab.id) { mutableStateOf(tab.title) }
        AlertDialog(
            onDismissRequest = { tabBeingRenamed = null },
            title = { Text("Rename tab") },
            text = {
                OutlinedTextField(value = text, onValueChange = { text = it }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.renameTab(tab.id, text)
                    tabBeingRenamed = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { tabBeingRenamed = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun TabStrip(
    tabs: List<IdeTabEntity>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    onCloseRequest: (IdeTabEntity) -> Unit,
    onRenameRequest: (IdeTabEntity) -> Unit,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    onAddTab: () -> Unit,
) {
    val density = LocalDensity.current
    var draggedTabId by remember { mutableStateOf<String?>(null) }
    var dragOffsetX by remember { mutableFloatStateOf(0f) }
    val tabWidthsPx = remember { mutableMapOf<String, Int>() }
    // pointerInput below is keyed only on tab.id, so its gesture-detection
    // coroutine survives the reorders it triggers instead of being cancelled
    // by them; it reads the current tab list via this instead of closing
    // over the tabs parameter directly, which would go stale in that case.
    val currentTabs by rememberUpdatedState(tabs)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { tab ->
            val isDragged = draggedTabId == tab.id
            TabChip(
                tab = tab,
                selected = tab.id == selectedId,
                onClick = { onSelect(tab.id) },
                onClose = if (tabs.size > 1) ({ onCloseRequest(tab) }) else null,
                modifier = Modifier
                    .onSizeChanged { tabWidthsPx[tab.id] = it.width }
                    .let { base ->
                        if (isDragged) base.offset { IntOffset(dragOffsetX.roundToInt(), 0) } else base
                    }
                    .pointerInput(tab.id) {
                        var totalDragDistance = 0f
                        detectDragGesturesAfterLongPress(
                            onDragStart = {
                                draggedTabId = tab.id
                                dragOffsetX = 0f
                                totalDragDistance = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffsetX += dragAmount.x
                                totalDragDistance += abs(dragAmount.x)
                                val currentIndex = currentTabs.indexOfFirst { it.id == tab.id }
                                val myWidth = tabWidthsPx[tab.id]?.toFloat() ?: return@detectDragGesturesAfterLongPress
                                if (dragOffsetX > myWidth / 2 && currentIndex < currentTabs.lastIndex) {
                                    onReorder(currentIndex, currentIndex + 1)
                                    dragOffsetX -= myWidth
                                } else if (dragOffsetX < -myWidth / 2 && currentIndex > 0) {
                                    onReorder(currentIndex, currentIndex - 1)
                                    dragOffsetX += myWidth
                                }
                            },
                            onDragEnd = {
                                if (totalDragDistance < with(density) { 8.dp.toPx() }) {
                                    onRenameRequest(tab)
                                }
                                draggedTabId = null
                                dragOffsetX = 0f
                            },
                            onDragCancel = {
                                draggedTabId = null
                                dragOffsetX = 0f
                            },
                        )
                    },
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
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        color = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier,
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
