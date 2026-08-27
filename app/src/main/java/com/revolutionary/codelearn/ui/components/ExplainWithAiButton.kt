package com.revolutionary.codelearn.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

private val FREE_AI_ASSISTANTS = listOf(
    "ChatGPT" to "https://chatgpt.com/",
    "Microsoft Copilot" to "https://copilot.microsoft.com/",
    "DeepSeek" to "https://chat.deepseek.com/",
)

/**
 * A help button that copies a ready-made prompt (the lesson's challenge, plus
 * whatever the learner has typed so far) to the clipboard and hands off to a
 * free web-based AI assistant of the learner's choice, since the app doesn't
 * ship its own AI backend.
 */
@Composable
fun ExplainWithAiButton(buildPrompt: () -> String) {
    val context = LocalContext.current
    var showChooser by remember { mutableStateOf(false) }

    TextButton(onClick = { showChooser = true }) {
        Text("✨ Explain with AI")
    }

    if (showChooser) {
        AlertDialog(
            onDismissRequest = { showChooser = false },
            title = { Text("Ask a free AI assistant") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Copies this challenge to your clipboard, then opens the assistant so you can paste it in:")
                    FREE_AI_ASSISTANTS.forEach { (name, url) ->
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                copyPromptAndOpen(context, buildPrompt(), url)
                                showChooser = false
                            },
                        ) {
                            Text(name, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showChooser = false }) { Text("Cancel") }
            },
        )
    }
}

private fun copyPromptAndOpen(context: Context, prompt: String, url: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("CodeLearn prompt", prompt))
    Toast.makeText(context, "Prompt copied — paste it into the chat", Toast.LENGTH_SHORT).show()
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}
