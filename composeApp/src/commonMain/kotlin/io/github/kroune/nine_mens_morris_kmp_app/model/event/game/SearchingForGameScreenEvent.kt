package io.github.kroune.nine_mens_morris_kmp_app.model.event.game

sealed interface SearchingForGameScreenEvent {
    data object Back : SearchingForGameScreenEvent
}