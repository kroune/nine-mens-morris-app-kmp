import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithBotScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithBotScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val gameWithBot by story {
    AppTheme {
        GameWithBotScreen(
            GameWithBotScreenState(
                gameStartPosition,
                setOf(),
                null,
                false
            )
        ) {}
    }
}