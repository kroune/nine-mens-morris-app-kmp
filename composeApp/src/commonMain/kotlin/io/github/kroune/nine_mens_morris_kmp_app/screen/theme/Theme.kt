package io.github.kroune.nine_mens_morris_kmp_app.screen.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
inline fun AppTheme(crossinline content: @Composable () -> Unit) {
    val colorScheme = when (isSystemInDarkTheme()) {
        false -> lightColorScheme(
            background = Color.LightGray
        )
        true -> darkColorScheme(
            background = Color.DarkGray
        )
    }
    val extendedColorScheme = when (isSystemInDarkTheme()) {
        false -> lightExtendedColors()
        true -> darkExtendedColors()
    }
    MaterialTheme(colorScheme) {
        ExtendedColorTheme(extendedColorScheme) {
            content()
        }
    }
}