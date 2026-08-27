package com.revolutionary.codelearn.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import com.revolutionary.codelearn.core.model.Language

fun symbolsFor(language: Language): List<String> = when (language) {
    Language.LUA -> listOf("print", "if", "then", "else", "elseif", "end", "local", "==", "(", ")", "\"", "'", "=", "[", "]")
    Language.PYTHON -> listOf("print", "if", "elif", "else", "def", "return", "==", "(", ")", "\"", "'", "=", "[", "]", ":")
    Language.CPP -> listOf("cout", "if", "else", "int", "return", "==", "(", ")", "\"", "=", "{", "}", ";", "<<")
}

@Composable
fun SymbolToolbar(language: Language, onSymbolTapped: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        symbolsFor(language).forEach { symbol ->
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
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        )
    }
}
