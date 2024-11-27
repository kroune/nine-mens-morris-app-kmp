package com.kroune.nine_mens_morris_kmp_app.event

sealed interface SignInScreenEvent {
    data object Back: SignInScreenEvent
    data object Login: SignInScreenEvent
    data object SwitchToSignInScreen: SignInScreenEvent
}