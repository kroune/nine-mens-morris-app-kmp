package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface PastGamesApiResponse<out T> {
    class Success<T>(val playedGames: T) : PastGamesApiResponse<T>
    class CredentialsError : PastGamesApiResponse<Nothing>
    class NetworkError : PastGamesApiResponse<Nothing>
    class ServerError : PastGamesApiResponse<Nothing>
    class UnknownError : PastGamesApiResponse<Nothing>
}