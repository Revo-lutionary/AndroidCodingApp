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
 *
 * [onEditorReady] hands back the underlying [CodeEditor] so callers (e.g. a
 * symbol toolbar) can insert text at the current cursor position via
 * [insertAtCursor].
 */
@Composable
fun CodeEditorField(
    code: String,
    onCodeChange: (String) -> Unit,
    language: Language,
    modifier: Modifier = Modifier,
    onEditorReady: (CodeEditor) -> Unit = {},
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            CodeEditor(context).apply {
                setText(code)
                subscribeEvent(ContentChangeEvent::class.java) { _, _ ->
                    onCodeChange(text.toString())
                }
                onEditorReady(this)
            }
        },
        update = { editor ->
            if (editor.text.toString() != code) {
                editor.setText(code)
            }
        },
    )
}

/** Inserts [text] at the editor's current cursor position. */
fun CodeEditor.insertAtCursor(text: String) {
    val cursor = this.cursor
    this.text.insert(cursor.leftLine, cursor.leftColumn, text)
}
