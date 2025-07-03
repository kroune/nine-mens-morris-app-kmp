import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signUpScreen.SignUpScreen
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.AppTheme
import org.jetbrains.compose.storytale.story


val signUp by story {
    AppTheme {
        SignUpScreen(
            SignUpScreenState(
                "some username",
                false,
                "some password",
                false,
                "repeated password",
                false,
                null,
                null,
                false
            )
        ) {}
    }
}

val signUp2 by story {
    AppTheme {
        SignUpScreen(
            SignUpScreenState(
                "some username",
                false,
                "some password",
                false,
                "repeated password",
                false,
                null,
                null,
                false
            )
        ) {}
    }
}

val signUp3 by story {
    AppTheme {
        SignUpScreen(
            SignUpScreenState(
                "some username",
                false,
                "some password",
                false,
                "repeated password",
                false,
                null,
                null,
                false
            )
        ) {}
    }
}
