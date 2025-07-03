import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.game.OnlineGameScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.onlineGameScreen.OnlineGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val onlineGame by story {
    AppTheme {
        OnlineGameScreen(
            OnlineGameScreenState(
                gameStartPosition,
                null,
                true,
                23,
                setOf(),
                false,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
            )
        ) {}
    }
}