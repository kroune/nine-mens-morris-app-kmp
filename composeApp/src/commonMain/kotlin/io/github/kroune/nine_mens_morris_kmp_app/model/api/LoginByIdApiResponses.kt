package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface LoginByIdApiResponses {
    class Success(val login: String): LoginByIdApiResponses
    class CredentialsError: LoginByIdApiResponses
    class NetworkError: LoginByIdApiResponses
    class ServerError: LoginByIdApiResponses
    class UnknownError: LoginByIdApiResponses
}