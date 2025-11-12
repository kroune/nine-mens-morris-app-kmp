package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.game

sealed interface SearchingForGameScreenEvent {
    data object Back : SearchingForGameScreenEvent
}
