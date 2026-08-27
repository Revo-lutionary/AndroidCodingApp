package com.revolutionary.codelearn.core.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.revolutionary.codelearn.core.model.Language
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula

private val AUTO_PAIR_CLOSERS = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '"' to '"',
    '\'' to '\'',
)

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
                colorScheme = SchemeDarcula()
                setText(code)

                var lastKnownText = code
                var isAutoPairing = false
                subscribeEvent(ContentChangeEvent::class.java) { _, _ ->
                    if (!isAutoPairing) {
                        val newlyTypedOpener = singleCharInserted(lastKnownText, text.toString())
                        val closer = newlyTypedOpener?.let { AUTO_PAIR_CLOSERS[it] }
                        if (closer != null) {
                            isAutoPairing = true
                            val line = cursor.leftLine
                            val column = cursor.leftColumn
                            this.text.insert(line, column, closer.toString())
                            setSelection(line, column)
                            isAutoPairing = false
                        }
                    }
                    lastKnownText = text.toString()
                    onCodeChange(text.toString())
                }
                // Sora-Editor reacts to IME window insets on its own to keep the
                // cursor visible above the keyboard. That fights with Compose's
                // own Modifier.imePadding() on the screen around it, stacking
                // two keyboard-height shifts into one large gap. Since Compose
                // already handles keyboard avoidance here, tell this view to
                // leave insets alone.
                ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets -> insets }
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

/** If [newText] is [oldText] with exactly one character inserted, returns that character. */
private fun singleCharInserted(oldText: String, newText: String): Char? {
    if (newText.length != oldText.length + 1) return null
    var i = 0
    while (i < oldText.length && i < newText.length && oldText[i] == newText[i]) i++
    if (i >= newText.length) return null
    if (oldText.substring(i) != newText.substring(i + 1)) return null
    return newText[i]
}

/**
 * Inserts [text] at the editor's current cursor position. When [text] is a
 * single opening bracket/quote, also inserts the matching closer and leaves
 * the cursor between the pair, matching the behavior for typed input.
 */
fun CodeEditor.insertAtCursor(text: String) {
    val cursor = this.cursor
    val line = cursor.leftLine
    val column = cursor.leftColumn
    val closer = text.singleOrNull()?.let { AUTO_PAIR_CLOSERS[it] }
    if (closer != null) {
        this.text.insert(line, column, text + closer)
        setSelection(line, column + text.length)
    } else {
        this.text.insert(line, column, text)
    }
}
