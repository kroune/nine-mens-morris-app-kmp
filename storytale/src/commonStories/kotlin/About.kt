import io.github.kroune.nine_mens_morris_kmp_app.component.other.aboutScreenComponent.AboutScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.AppInfo
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.AboutScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

val about by story {
    AppTheme {
        AboutScreen(
            state = AboutScreenState(
                telegram = AppInfo("https://t.me/LichnyiSvetM"),
                github = AppInfo("https://github.com/kroune/nine-mens-morris-app-kmp"),
                githubIssue = AppInfo("https://github.com/kroune/nine-mens-morris-app-kmp/issues/new")
            ),
            onEvent = {}
        )
    }
}