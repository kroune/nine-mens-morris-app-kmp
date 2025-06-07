package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game

sealed interface GameWithBotScreenEvent {
    data class OnPieceClick(val index: Int): GameWithBotScreenEvent
    data object Undo: GameWithBotScreenEvent
    data object Redo: GameWithBotScreenEvent
    data object Back: GameWithBotScreenEvent
}