package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountId

import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfoInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.accountIdDataSource

class AccountIdInteractorImpl : AccountIdInteractorI {
    private val local = accountIdDataSource

    override suspend fun getAccountId(): Result<Long> {
        val localAccountId = local.getAccountId()
        if (localAccountId != null) {
            return Result.success(localAccountId)
        }
        if (jwtTokenInteractor.getJwtToken() != null) {
            val remoteAccountId = accountInfoInteractor.getOwnAccountId()
            remoteAccountId.onSuccess {
                updateAccountId(it)
            }
            return remoteAccountId
        }
        throw IllegalStateException("we have not logged into any account")
    }

    override fun updateAccountId(newAccountId: Long) {
        return local.updateAccountId(newAccountId)
    }
}