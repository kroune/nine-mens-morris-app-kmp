package io.github.kroune.nine_mens_morris_kmp_app.model

sealed interface GiveUpApiResponse {
    data object Success: GiveUpApiResponse
    data object NetworkError: GiveUpApiResponse
    data object ServerError: GiveUpApiResponse
    data object UnknownError: GiveUpApiResponse
}