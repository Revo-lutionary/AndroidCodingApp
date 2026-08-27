package com.revolutionary.codelearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.revolutionary.codelearn.core.model.Language

private data class SymbolRows(val keywords: List<String>, val punctuation: List<String>)

private fun symbolsFor(language: Language): SymbolRows = when (language) {
    Language.LUA -> SymbolRows(
        keywords = listOf("print", "if", "then", "else", "elseif", "end", "local"),
        punctuation = listOf("==", "(", ")", "\"", "'", "=", "[", "]"),
    )
    Language.PYTHON -> SymbolRows(
        keywords = listOf("print", "if", "elif", "else", "def", "return"),
        punctuation = listOf("==", "(", ")", "\"", "'", "=", "[", "]", ":"),
    )
    Language.CPP -> SymbolRows(
        keywords = listOf("cout", "if", "else", "int", "return"),
        punctuation = listOf("==", "(", ")", "\"", "=", "{", "}", ";", "<<"),
    )
}

@Composable
fun SymbolToolbar(language: Language, onSymbolTapped: (String) -> Unit, modifier: Modifier = Modifier) {
    val rows = symbolsFor(language)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        SymbolRow(rows.keywords, onSymbolTapped)
        SymbolRow(rows.punctuation, onSymbolTapped)
    }
}

@Composable
private fun SymbolRow(symbols: List<String>, onSymbolTapped: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        symbols.forEach { symbol ->
            SymbolChip(symbol = symbol, onClick = { onSymbolTapped(symbol) })
        }
    }
}

@Composable
private fun SymbolChip(symbol: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Text(
            text = symbol,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontFamily = FontFamily.Monospace,
        )
    }
}
