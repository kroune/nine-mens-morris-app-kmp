package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface SearchingForGameResponse {
    class Success(val gameId: Long): SearchingForGameResponse
    class NetworkError: SearchingForGameResponse
    class ServerError: SearchingForGameResponse
    class UnknownError: SearchingForGameResponse
}