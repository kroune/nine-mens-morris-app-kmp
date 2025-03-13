package io.github.kroune.nine_mens_morris_kmp_app.screen.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

fun lightExtendedColors(): ExtendedColorScheme {
    return ExtendedColorScheme(
        linkColors = ButtonColors(
            containerColor = Color.Unspecified,
            contentColor = Color(85, 104, 218),
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color(71, 86, 177)
        )
    )
}

fun darkExtendedColors(): ExtendedColorScheme {
    return ExtendedColorScheme(
        linkColors = ButtonColors(
            containerColor = Color.Unspecified,
            contentColor = Color(53, 74, 188),
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color(37, 50, 142)
        )
    )
}

@Immutable
data class ExtendedColorScheme(
    val linkColors: ButtonColors,
)

internal val LocalColorScheme = staticCompositionLocalOf { lightExtendedColors() }

@Composable
fun ExtendedColorTheme(
    colorScheme: ExtendedColorScheme = lightExtendedColors(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColorScheme provides colorScheme,
    ) {
        content()
    }
}

object ExtendedColorTheme {
    val colorScheme: ExtendedColorScheme
        @Composable @ReadOnlyComposable get() = LocalColorScheme.current
}