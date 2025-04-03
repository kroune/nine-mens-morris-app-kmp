package io.github.kroune.nine_mens_morris_kmp_app.interactors.accountId

import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses

interface AccountIdInteractorI {
    suspend fun getAccountId(): AccountIdByJwtTokenApiResponses
    fun updateAccountId(newAccountId: Long)
}