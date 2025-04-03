package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface RegisterApiResponses {
    data class Success(val jwtToken: String): RegisterApiResponses
    class LoginAlreadyInUse: RegisterApiResponses
    class NetworkError: RegisterApiResponses
    class ServerError: RegisterApiResponses
    class UnknownError: RegisterApiResponses
}