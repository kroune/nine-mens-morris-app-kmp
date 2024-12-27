package com.kroune.nine_mens_morris_kmp_app.event.auth

sealed interface SignInScreenEvent {
    data object Back: SignInScreenEvent
    data object Login: SignInScreenEvent
    data object SwitchToSignInScreen: SignInScreenEvent
    data class UsernameUpdate(val newText: String): SignInScreenEvent
    data class PasswordUpdate(val newText: String): SignInScreenEvent
}