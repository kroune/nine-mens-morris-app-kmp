package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.auth

sealed interface SignInScreenEvent {
    data object NavigateBack: SignInScreenEvent
    data object Login: SignInScreenEvent
    data object SwitchToSignInScreen: SignInScreenEvent
    data class UsernameUpdate(val newText: String): SignInScreenEvent
    data class PasswordUpdate(val newText: String): SignInScreenEvent
}
