package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface RatingByIdApiResponses {
    class Success(val rating: Long): RatingByIdApiResponses
    class CredentialsError: RatingByIdApiResponses
    class ServerError: RatingByIdApiResponses
    class NetworkError: RatingByIdApiResponses
    class UnknownError: RatingByIdApiResponses
}
