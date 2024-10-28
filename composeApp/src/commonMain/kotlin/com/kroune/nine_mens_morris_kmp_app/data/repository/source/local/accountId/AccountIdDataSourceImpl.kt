package com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.accountId

import com.russhwolf.settings.Settings

class AccountIdDataSourceImpl: AccountIdDataSourceI {
    override fun deleteAccountId() {
        Settings().remove("accountId")
    }
    override fun getAccountId(): Long? {
        return Settings().getLongOrNull("accountId")
    }

    override fun updateAccountId(newAccountId: Long) {
        return Settings().putLong("accountId", newAccountId)
    }
}