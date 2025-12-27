package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface LeaderboardApiResponses {
    class Success(val leaderboard: List<Long>): LeaderboardApiResponses
    class CredentialsError: LeaderboardApiResponses
    class NetworkError: LeaderboardApiResponses
    class ServerError: LeaderboardApiResponses
    class UnknownError: LeaderboardApiResponses
}
