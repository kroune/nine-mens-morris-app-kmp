package io.github.kroune.nine_mens_morris_kmp_app.event.auth

sealed interface SignUpScreenEvent {
    data class UpdateUsername(val newUsername: String): SignUpScreenEvent
    data class UpdatePassword(val newPassword: String): SignUpScreenEvent
    data class UpdateRepeatedPassword(val newRepeatedPassword: String): SignUpScreenEvent
    data object Back: SignUpScreenEvent
    data object Register: SignUpScreenEvent
    data object SwitchToSignInScreen: SignUpScreenEvent
}