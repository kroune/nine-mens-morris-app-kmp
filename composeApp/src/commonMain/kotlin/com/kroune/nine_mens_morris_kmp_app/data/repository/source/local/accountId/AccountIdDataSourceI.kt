package com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId

interface AccountIdDataSourceI {
    fun deleteAccountId()
    fun getAccountId(): Long?
    fun updateAccountId(newAccountId: Long)
}