package com.kroune.nine_mens_morris_kmp_app.event

sealed interface ScreenAEvent {
    data object ClickButton: ScreenAEvent
    data class UpdateText(val string: String): ScreenAEvent
}