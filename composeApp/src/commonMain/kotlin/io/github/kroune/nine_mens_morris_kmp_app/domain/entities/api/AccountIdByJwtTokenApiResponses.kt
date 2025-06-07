package io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api

sealed interface AccountIdByJwtTokenApiResponses {
    class Success(val accountId: Long): AccountIdByJwtTokenApiResponses
    class CredentialsError: AccountIdByJwtTokenApiResponses
    class NetworkError: AccountIdByJwtTokenApiResponses
    class ServerError: AccountIdByJwtTokenApiResponses
    class UnknownError: AccountIdByJwtTokenApiResponses
}