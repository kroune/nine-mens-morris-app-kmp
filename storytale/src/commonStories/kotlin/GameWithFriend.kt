import androidx.compose.runtime.mutableStateListOf
import com.kroune.nineMensMorrisLib.gameStartPosition
import io.github.kroune.nine_mens_morris_kmp_app.component.game.GameWithFriendScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.GameWithFriendScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val gameWithFriend by story {
    AppTheme {
        GameWithFriendScreen(
            state = GameWithFriendScreenState(
                position = gameStartPosition,
                moveHints = setOf(),
                selectedButton = null,
                depth = 4,
                gameEnded = false,
                gameAnalyzePositions = mutableStateListOf()
            ),
            onEvent = {}
        )
    }
}

// Story with game ended state to show the popup
val gameWithFriendEnded by story {
    AppTheme {
        GameWithFriendScreen(
            state = GameWithFriendScreenState(
                position = gameStartPosition,
                moveHints = setOf(),
                selectedButton = null,
                depth = 4,
                gameEnded = true,
                gameAnalyzePositions = mutableStateListOf()
            ),
            onEvent = {}
        )
    }
}