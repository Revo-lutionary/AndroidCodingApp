package com.revolutionary.codelearn.ui.screens.roadmap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.revolutionary.codelearn.core.model.NodeType
import com.revolutionary.codelearn.core.model.RoadmapNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapScreen(
    languageId: String,
    onNodeSelected: (trackId: String, node: RoadmapNode) -> Unit,
    onBack: () -> Unit,
    viewModel: RoadmapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(languageId) {
        viewModel.load(languageId)
    }

    val completedCount = uiState.uiNodes.count { it.state == RoadmapNodeState.COMPLETED }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.trackTitle.ifEmpty { languageId }) },
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
            } else if (uiState.uiNodes.isEmpty()) {
                Text(
                    text = "No lessons yet for this language — check back soon.",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "$completedCount / ${uiState.uiNodes.size} completed",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(28.dp),
                    ) {
                        itemsIndexed(uiState.uiNodes) { index, uiNode ->
                            RoadmapNodeRow(
                                index = index,
                                uiNode = uiNode,
                                onClick = {
                                    if (uiNode.state != RoadmapNodeState.LOCKED) {
                                        onNodeSelected(uiState.trackId, uiNode.node)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private val ZIGZAG_PATTERN = listOf(0, 56, 0, -56)

@Composable
private fun RoadmapNodeRow(index: Int, uiNode: RoadmapUiNode, onClick: () -> Unit) {
    val offsetX = ZIGZAG_PATTERN[index % ZIGZAG_PATTERN.size].dp

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.offset(x = offsetX).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (uiNode.state == RoadmapNodeState.CURRENT) {
                Text(
                    text = "Up next",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 6.dp),
                )
            }
            NodeBubble(uiNode = uiNode, onClick = onClick)
            Text(
                text = uiNode.node.title,
                modifier = Modifier.padding(top = 8.dp),
                color = if (uiNode.state == RoadmapNodeState.LOCKED) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun NodeBubble(uiNode: RoadmapUiNode, onClick: () -> Unit) {
    val (background, iconColor) = when (uiNode.state) {
        RoadmapNodeState.COMPLETED -> MaterialTheme.colorScheme.primary to Color.White
        RoadmapNodeState.CURRENT -> MaterialTheme.colorScheme.secondary to Color.White
        RoadmapNodeState.LOCKED -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(enabled = uiNode.state != RoadmapNodeState.LOCKED, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        val icon = when {
            uiNode.state == RoadmapNodeState.COMPLETED -> Icons.Default.Check
            uiNode.state == RoadmapNodeState.LOCKED -> Icons.Default.Lock
            uiNode.node.type == NodeType.QUIZ -> Icons.Default.Star
            else -> Icons.Default.PlayArrow
        }
        Icon(icon, contentDescription = null, tint = iconColor)
    }
}
