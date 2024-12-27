package com.kroune.nine_mens_morris_kmp_app.event.other

sealed interface AppStartAnimationScreenEvent {
    data object ClickButton: AppStartAnimationScreenEvent
}