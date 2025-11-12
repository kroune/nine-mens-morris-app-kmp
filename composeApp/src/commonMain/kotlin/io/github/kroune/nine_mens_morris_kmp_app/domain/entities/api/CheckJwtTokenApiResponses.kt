package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface CheckJwtTokenApiResponses {
    class Success(val result: Boolean): CheckJwtTokenApiResponses
    class ServerError: CheckJwtTokenApiResponses
    class NetworkError: CheckJwtTokenApiResponses
    class UnknownError: CheckJwtTokenApiResponses
}
