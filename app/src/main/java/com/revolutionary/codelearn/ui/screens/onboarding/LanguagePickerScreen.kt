package com.revolutionary.codelearn.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.revolutionary.codelearn.core.model.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerScreen(
    onLanguageSelected: (Language) -> Unit,
    onIdeClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CodeLearn") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
        ) {
            Text(
                text = "What do you want to learn?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Pick a language to start your first roadmap.",
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Language.entries) { language ->
                    Card(
                        onClick = { onLanguageSelected(language) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(),
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(text = language.displayName, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = languageTagline(language), modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
                item {
                    OutlinedCard(
                        onClick = onIdeClick,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Column {
                                Text(text = "IDE", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = "Just test and run some code.",
                                    modifier = Modifier.padding(top = 4.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun languageTagline(language: Language): String = when (language) {
    Language.PYTHON -> "Readable, beginner-friendly, everywhere."
    Language.LUA -> "Lightweight scripting, popular in games like Roblox."
    Language.CPP -> "Fast and low-level — how systems get built."
}
