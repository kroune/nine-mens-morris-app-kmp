package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface RequiredVersionApiResponse {
    data class Success(val requiredVersion: Int?): RequiredVersionApiResponse
    class NetworkError: RequiredVersionApiResponse
    class ServerError: RequiredVersionApiResponse
    class UnknownError: RequiredVersionApiResponse
}
