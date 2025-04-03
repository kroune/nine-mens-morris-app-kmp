package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface LoginByIdApiResponses {
    data class Success(val login: String): LoginByIdApiResponses
    data object CredentialsError: LoginByIdApiResponses
    data object NetworkError: LoginByIdApiResponses
    data object ServerError: LoginByIdApiResponses
    data object UnknownError: LoginByIdApiResponses
}