package com.revolutionary.codelearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.revolutionary.codelearn.core.editor.CodeEditorField
import com.revolutionary.codelearn.core.editor.insertAtCursor
import com.revolutionary.codelearn.core.model.ExecutionResult
import com.revolutionary.codelearn.core.model.Language
import io.github.rosemoe.sora.widget.CodeEditor

/**
 * The editor + symbol toolbar + Run button + output panel, shared between a
 * lesson's Code tab and the standalone IDE playground.
 */
@Composable
fun CodePlaygroundArea(
    language: Language,
    code: String,
    onCodeChange: (String) -> Unit,
    isRunning: Boolean,
    result: ExecutionResult?,
    onRun: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var editorRef by remember { mutableStateOf<CodeEditor?>(null) }

    Column(modifier = modifier.imePadding()) {
        CodeEditorField(
            code = code,
            onCodeChange = onCodeChange,
            language = language,
            modifier = Modifier.fillMaxWidth().weight(1f),
            onEditorReady = { editorRef = it },
        )

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
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            Button(onClick = onRun, enabled = !isRunning) {
                if (isRunning) {
                    CircularProgressIndicator(modifier = Modifier.height(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Text(" Run")
                }
            }
        }
    }
}
