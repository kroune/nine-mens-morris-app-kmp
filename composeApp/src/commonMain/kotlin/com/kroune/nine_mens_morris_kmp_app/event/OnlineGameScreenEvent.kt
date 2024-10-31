package com.kroune.nine_mens_morris_kmp_app.event

sealed interface OnlineGameScreenEvent {
    data object GiveUp: OnlineGameScreenEvent
    data class Click(val index: Int): OnlineGameScreenEvent
}