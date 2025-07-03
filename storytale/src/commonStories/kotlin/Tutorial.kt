import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.TutorialScreen
import org.jetbrains.compose.storytale.story

val tutorial by story {
    AppTheme {
        TutorialScreen()
    }
}