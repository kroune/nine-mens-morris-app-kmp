package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game

sealed interface OnlineGameScreenEvent {
    data object GiveUp: OnlineGameScreenEvent
    data object GiveUpDiscarded: OnlineGameScreenEvent
    data class Click(val index: Int): OnlineGameScreenEvent
    data object NavigateToMainScreen: OnlineGameScreenEvent
    data object NavigateToAccountView: OnlineGameScreenEvent
    data object NavigateToOwnAccountView : OnlineGameScreenEvent
    data class ReloadIcon(val ownAccount: Boolean): OnlineGameScreenEvent
    data class ReloadName(val ownAccount: Boolean): OnlineGameScreenEvent
    data class ReloadRating(val ownAccount: Boolean): OnlineGameScreenEvent
}