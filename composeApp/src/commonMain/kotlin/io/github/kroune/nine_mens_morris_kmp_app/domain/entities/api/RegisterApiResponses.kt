package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface RegisterApiResponses {
    class Success(val jwtToken: String): RegisterApiResponses
    class LoginAlreadyInUse: RegisterApiResponses
    class NetworkError: RegisterApiResponses
    class ServerError: RegisterApiResponses
    class UnknownError: RegisterApiResponses
}
