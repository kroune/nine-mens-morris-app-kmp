package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface LeaderboardApiResponses {
    data class Success(val leaderboard: List<Long>): LeaderboardApiResponses
    data object CredentialsError: LeaderboardApiResponses
    data object NetworkError: LeaderboardApiResponses
    data object ServerError: LeaderboardApiResponses
    data object UnknownError: LeaderboardApiResponses
}