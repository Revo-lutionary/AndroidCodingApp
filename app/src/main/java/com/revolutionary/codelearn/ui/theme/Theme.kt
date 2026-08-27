package com.revolutionary.codelearn.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = CodeLearnGreen,
    secondary = CodeLearnGreenDark,
    background = CodeLearnBackground,
    surface = CodeLearnSurface,
)

private val LightColors = lightColorScheme(
    primary = CodeLearnGreenDark,
    secondary = CodeLearnGreen,
)

@Composable
fun CodeLearnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CodeLearnTypography,
        content = content,
    )
}
