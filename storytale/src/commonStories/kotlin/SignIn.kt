import io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenState
import io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signInScreen.SignInScreen
import org.jetbrains.compose.storytale.story


val signIn by story {
    SignInScreen(
        SignInScreenState(
            "some username",
            false,
            "some invalid password",
            false,
            null,
            null,
            false
        )
    ) {}
}

val signIn2 by story {
    SignInScreen(
        SignInScreenState(
            "someusername",
            true,
            "someValidPassword",
            true,
            null,
            null,
            false
        )
    ) {}
}

val signIn3 by story {
    SignInScreen(
        SignInScreenState(
            "someusername",
            true,
            "someValidPassword",
            true,
            null,
            null,
            true
        )
    ) {}
}
