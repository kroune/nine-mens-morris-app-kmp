package com.kroune.nine_mens_morris_kmp_app.event

sealed interface SearchingForGameScreenEvent {
    data object Back : SearchingForGameScreenEvent
}