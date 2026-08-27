package com.revolutionary.codelearn.core.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.revolutionary.codelearn.core.model.Language
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.CodeEditor

/**
 * Wraps Sora-Editor's [CodeEditor] View for use in Compose. Per-language
 * TextMate syntax highlighting is wired in a later phase; this establishes
 * the module boundary and the two-way text binding.
 */
@Composable
fun CodeEditorField(
    code: String,
    onCodeChange: (String) -> Unit,
    language: Language,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CodeEditor(context).apply {
                setText(code)
                subscribeEvent(ContentChangeEvent::class.java) { _, _ ->
                    onCodeChange(text.toString())
                }
            }
        },
        update = { editor ->
            if (editor.text.toString() != code) {
                editor.setText(code)
            }
        },
    )
}
