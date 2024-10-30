package com.kroune.nine_mens_morris_kmp_app.event

sealed interface GameWithBotEvent {
    data class OnPieceClick(val index: Int): GameWithBotEvent
    data object Undo: GameWithBotEvent
    data object Redo: GameWithBotEvent
}