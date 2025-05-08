package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface SendMoveApiResponse {
    data object Success: SendMoveApiResponse
    data object NetworkError: SendMoveApiResponse
    data object ServerError: SendMoveApiResponse
    data object UnknownError: SendMoveApiResponse
}