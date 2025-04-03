package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface SearchingForGameResponse {
    data class Success(val gameId: Long): SearchingForGameResponse
    class NetworkError: SearchingForGameResponse
    class ServerError: SearchingForGameResponse
    class UnknownError: SearchingForGameResponse
}