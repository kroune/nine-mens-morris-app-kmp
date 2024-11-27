package com.kroune.nine_mens_morris_kmp_app.event

sealed interface SignUpScreenEvent {
    data object Back: SignUpScreenEvent
    data object Register: SignUpScreenEvent
    data object SwitchToSignInScreen: SignUpScreenEvent
}