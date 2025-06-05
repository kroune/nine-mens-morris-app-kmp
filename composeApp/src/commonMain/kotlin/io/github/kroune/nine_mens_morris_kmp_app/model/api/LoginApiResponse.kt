package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface LoginApiResponse {
    data class Success(val jwtToken: String): LoginApiResponse
    class NetworkError: LoginApiResponse
    class CredentialsError: LoginApiResponse
    class ServerError: LoginApiResponse
    class UnknownError: LoginApiResponse
}