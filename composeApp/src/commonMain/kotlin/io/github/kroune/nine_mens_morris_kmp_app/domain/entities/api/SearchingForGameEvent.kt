package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface SearchingForGameEvent {

    sealed interface Success : SearchingForGameEvent {
        class GameFound(val gameId: Long) : Success
        class NewExpectedWaitingTime(val expectedWaitingTime: Long) : Success
    }

    sealed interface Error : SearchingForGameEvent {
        object NetworkError : Error
        object ServerError : Error
        object UnknownError : Error
    }
}
