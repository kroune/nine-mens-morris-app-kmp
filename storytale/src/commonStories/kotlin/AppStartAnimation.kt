import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AppStartAnimationScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val appStartAnimation by story {
    AppTheme {
        AppStartAnimationScreen {}
    }
}