package com.kroune.nine_mens_morris_kmp_app.event

sealed interface SignInScreenEvent {
    object Login: SignInScreenEvent
    object SwitchToSignInScreen: SignInScreenEvent
}