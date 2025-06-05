package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface RatingByIdApiResponses {
    data class Success(val rating: Long): RatingByIdApiResponses
    data object CredentialsError: RatingByIdApiResponses
    data object ServerError: RatingByIdApiResponses
    data object NetworkError: RatingByIdApiResponses
    data object UnknownError: RatingByIdApiResponses
}
