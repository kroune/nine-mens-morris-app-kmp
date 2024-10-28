package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountId

interface AccountIdInteractorI {
    suspend fun getAccountId(): Result<Long>
    fun updateAccountId(newAccountId: Long)
}