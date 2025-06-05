package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface CheckJwtTokenApiResponses {
    data class Success(val result: Boolean): CheckJwtTokenApiResponses
    data object ServerError: CheckJwtTokenApiResponses
    data object NetworkError: CheckJwtTokenApiResponses
    data object UnknownError: CheckJwtTokenApiResponses
}