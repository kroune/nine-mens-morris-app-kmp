package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountId

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses

interface AccountIdRepositoryI {
    suspend fun getAccountId(): AccountIdByJwtTokenApiResponses
    fun updateAccountId(newAccountId: Long)
}
