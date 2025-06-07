package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface AppLastVersionApiResponse {
    data class Success(val lastVersion: Int?): AppLastVersionApiResponse
    class NetworkError: AppLastVersionApiResponse
    class ServerError: AppLastVersionApiResponse
    class UnknownError: AppLastVersionApiResponse
}