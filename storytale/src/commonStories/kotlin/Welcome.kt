import io.github.kroune.nine_mens_morris_kmp_app.component.other.welcomeScreenComponent.WelcomeScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen.WelcomeScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story

// Story for a user who is logged in and has seen the tutorial
val welcomeLoggedIn by story {
    AppTheme {
        WelcomeScreen(
            state = WelcomeScreenState(
                isInAccount = CheckJwtTokenApiResponses.Success(true),
                accountIdFailure = null,
                hasSeenTutorial = true
            ),
            onEvent = {}
        )
    }
}

// Story for a user who is not logged in and has not seen the tutorial
val welcomeNotLoggedIn by story {
    AppTheme {
        WelcomeScreen(
            state = WelcomeScreenState(
                isInAccount = CheckJwtTokenApiResponses.Success(false),
                accountIdFailure = null,
                hasSeenTutorial = false
            ),
            onEvent = {}
        )
    }
}

// Story for a user who is experiencing a network error
val welcomeNetworkError by story {
    AppTheme {
        WelcomeScreen(
            state = WelcomeScreenState(
                isInAccount = CheckJwtTokenApiResponses.NetworkError(),
                accountIdFailure = null,
                hasSeenTutorial = true
            ),
            onEvent = {}
        )
    }
}