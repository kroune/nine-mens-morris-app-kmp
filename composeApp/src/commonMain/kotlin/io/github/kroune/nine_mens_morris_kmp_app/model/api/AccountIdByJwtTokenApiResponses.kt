package io.github.kroune.nine_mens_morris_kmp_app.model.api

sealed interface AccountIdByJwtTokenApiResponses {
    data class Success(val accountId: Long): AccountIdByJwtTokenApiResponses
    class CredentialsError: AccountIdByJwtTokenApiResponses
    class NetworkError: AccountIdByJwtTokenApiResponses
    class ServerError: AccountIdByJwtTokenApiResponses
    class UnknownError: AccountIdByJwtTokenApiResponses
}