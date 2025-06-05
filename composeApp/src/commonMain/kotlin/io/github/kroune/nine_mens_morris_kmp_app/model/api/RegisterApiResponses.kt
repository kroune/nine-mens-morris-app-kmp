package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface RegisterApiResponses {
    data class Success(val jwtToken: String): RegisterApiResponses
    class LoginAlreadyInUse: RegisterApiResponses
    class NetworkError: RegisterApiResponses
    class ServerError: RegisterApiResponses
    class UnknownError: RegisterApiResponses
}