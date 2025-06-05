package io.github.kroune.nine_mens_morris_kmp_app.interactors.accountId

import io.github.kroune.nine_mens_morris_kmp_app.data.accountIdDataSource
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses

class AccountIdInteractorImpl : AccountIdInteractorI {
    private val local = accountIdDataSource

    override suspend fun getAccountId(): AccountIdByJwtTokenApiResponses {
        val localAccountId = local.getAccountId()
        if (localAccountId != null) {
            return AccountIdByJwtTokenApiResponses.Success(localAccountId)
        }
        if (jwtTokenInteractor.getJwtToken() != null) {
            val remoteAccountId = accountInfoInteractor.getOwnAccountId()
            if (remoteAccountId is AccountIdByJwtTokenApiResponses.Success)
                updateAccountId(remoteAccountId.accountId)
            return remoteAccountId
        }
        throw error("we have not logged into any account")
    }

    override fun updateAccountId(newAccountId: Long) {
        return local.updateAccountId(newAccountId)
    }
}