package io.github.kroune.nine_mens_morris_kmp_app.repositories.accountId

import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses

interface AccountIdRepositoryI {
    suspend fun getAccountId(): AccountIdByJwtTokenApiResponses
    fun updateAccountId(newAccountId: Long)
}