package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface CreationDateByIdApiResponses {
    class Success(val creationDate: Triple<Int, Int, Int>): CreationDateByIdApiResponses
    class CredentialsError: CreationDateByIdApiResponses
    class ServerError: CreationDateByIdApiResponses
    class UnknownError: CreationDateByIdApiResponses
    class NetworkError: CreationDateByIdApiResponses
}