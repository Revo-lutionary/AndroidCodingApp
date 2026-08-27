package com.revolutionary.codelearn.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle

/** Renders `backtick`-delimited spans in [text] as monospace inline-code chips. */
fun renderInlineCode(text: String): AnnotatedString = buildAnnotatedString {
    val segments = text.split("`")
    segments.forEachIndexed { index, segment ->
        if (index % 2 == 1) {
            withStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = Color(0x22FFFFFF))) {
                append(segment)
            }
        } else {
            append(segment)
        }
    }
}
