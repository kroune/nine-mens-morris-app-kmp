package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses

class JwtTokenRepositoryImpl(
    private val local: JwtTokenRemoteDataSourceI,
    private val authRemoteDataSource: AuthRemoteDataSourceI,
    private val accountIdLocalDataSource: AccountIdLocalDataSourceI
) : JwtTokenRepositoryI {

    override fun logout() {
        local.deleteJwtToken()
        accountIdLocalDataSource.deleteAccountId()
    }

    override fun getJwtToken(): String? {
        return local.getJwtToken()
    }

    override suspend fun checkJwtToken(): CheckJwtTokenApiResponses {
        val jwtToken = getJwtToken() ?: return CheckJwtTokenApiResponses.Success(false)
        return authRemoteDataSource.checkJwtToken(jwtToken)
    }

    override fun updateJwtToken(newJwtToken: String) {
        return local.updateJwtToken(newJwtToken)
    }
}