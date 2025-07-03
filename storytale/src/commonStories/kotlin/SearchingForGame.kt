import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.searchingForGameScreen.SearchingForGameScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val searchingForGame by story {
    AppTheme {
        SearchingForGameScreen(
            SearchingForGameScreenState(
                null,
                12
            )
        )
    }
}