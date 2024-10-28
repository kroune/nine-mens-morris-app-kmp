package com.kroune.nine_mens_morris_kmp_app.event

sealed interface SignUpScreenEvent {
    object Register: SignUpScreenEvent
    object SwitchToSignInScreen: SignUpScreenEvent
}