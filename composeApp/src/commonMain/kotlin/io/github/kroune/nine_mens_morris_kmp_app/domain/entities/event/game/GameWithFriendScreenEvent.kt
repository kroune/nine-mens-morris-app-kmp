package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game

sealed interface GameWithFriendScreenEvent {
    sealed interface GameAnalyzeEvent: GameWithFriendScreenEvent {
        data object StartAnalyze: GameAnalyzeEvent
        data object IncreaseAnalyzeDepth: GameAnalyzeEvent
        data object DecreaseAnalyzeDepth: GameAnalyzeEvent
        data object CloseAnalyze: GameAnalyzeEvent
    }
    data class OnPieceClick(val index: Int): GameWithFriendScreenEvent
    data object Undo: GameWithFriendScreenEvent
    data object Redo: GameWithFriendScreenEvent
    data object NavigateBack: GameWithFriendScreenEvent
}