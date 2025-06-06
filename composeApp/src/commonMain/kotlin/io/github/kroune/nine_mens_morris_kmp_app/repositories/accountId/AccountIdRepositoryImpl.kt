package io.github.kroune.nine_mens_morris_kmp_app.repositories.accountId

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryI

class AccountIdRepositoryImpl(
    private val accountIdLocalDataSource: AccountIdLocalDataSourceI,
    private val accountInfoRepository: AccountInfoRepositoryI,
    private val jwtTokenRepository: JwtTokenRepositoryI,
) : AccountIdRepositoryI {

    override suspend fun getAccountId(): AccountIdByJwtTokenApiResponses {
        val localAccountId = accountIdLocalDataSource.getAccountId()
        if (localAccountId != null) {
            return AccountIdByJwtTokenApiResponses.Success(localAccountId)
        }
        if (jwtTokenRepository.getJwtToken() != null) {
            val remoteAccountId = accountInfoRepository.getOwnAccountId()
            if (remoteAccountId is AccountIdByJwtTokenApiResponses.Success)
                updateAccountId(remoteAccountId.accountId)
            return remoteAccountId
        }
        throw error("we have not logged into any account")
    }

    override fun updateAccountId(newAccountId: Long) {
        return accountIdLocalDataSource.updateAccountId(newAccountId)
    }
}