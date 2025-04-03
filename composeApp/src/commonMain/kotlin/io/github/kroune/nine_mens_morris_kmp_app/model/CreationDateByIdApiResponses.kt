package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface CreationDateByIdApiResponses {
    data class Success(val creationDate: Triple<Int, Int, Int>): CreationDateByIdApiResponses
    data object CredentialsError: CreationDateByIdApiResponses
    data object ServerError: CreationDateByIdApiResponses
    data object UnknownError: CreationDateByIdApiResponses
    data object NetworkError: CreationDateByIdApiResponses
}