package com.kroune.nine_mens_morris_kmp_app.event

sealed interface OnlineGameScreenEvent {
    data object GiveUp: OnlineGameScreenEvent
    data object GiveUpDiscarded: OnlineGameScreenEvent
    data class Click(val index: Int): OnlineGameScreenEvent
    data object NavigateToMainScreen: OnlineGameScreenEvent
    data class ReloadIcon(val ownAccount: Boolean): OnlineGameScreenEvent
    data class ReloadName(val ownAccount: Boolean): OnlineGameScreenEvent
    data class ReloadRating(val ownAccount: Boolean): OnlineGameScreenEvent
}