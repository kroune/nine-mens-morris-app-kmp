package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other

sealed interface AppStartAnimationScreenEvent {
    data object ClickButton: AppStartAnimationScreenEvent
}