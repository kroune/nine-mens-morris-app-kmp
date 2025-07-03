import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.RenderGameAnalyzeScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val gameAnalyze by story {
    AppTheme {
        RenderGameAnalyzeScreen(
            positions = emptyList(),
            depth = 3,
            onEvent = {}
        )
    }
}

// Story with positions to show the bottom sheet
val gameAnalyzeWithPositions by story {
    AppTheme {
        RenderGameAnalyzeScreen(
            positions = listOf(gameStartPosition),
            depth = 3,
            onEvent = {}
        )
    }
}
