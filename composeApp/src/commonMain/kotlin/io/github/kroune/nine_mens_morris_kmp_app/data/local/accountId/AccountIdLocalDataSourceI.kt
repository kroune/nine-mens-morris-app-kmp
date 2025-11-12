package io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId

interface AccountIdLocalDataSourceI {
    fun deleteAccountId()
    fun getAccountId(): Long?
    fun updateAccountId(newAccountId: Long)
}
