import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val gameBoard by story {
    AppTheme {
        RenderGameBoard(
            pos = gameStartPosition,
            selectedButton = null,
            moveHints = setOf(1, 2, 4)
        ) {
        }
    }
}