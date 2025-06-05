package io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtToken

import io.github.kroune.nine_mens_morris_kmp_app.data.accountIdDataSource
import io.github.kroune.nine_mens_morris_kmp_app.data.authRepository
import io.github.kroune.nine_mens_morris_kmp_app.data.jwtTokenDataSource
import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses

class JwtTokenInteractorImpl(
    private val local: JwtTokenDataSourceI = jwtTokenDataSource,
    private val remote: AuthRepositoryI = authRepository,
    private val localAccountId: AccountIdDataSourceI = accountIdDataSource
) : JwtTokenInteractorI {

    override fun logout() {
        local.deleteJwtToken()
        localAccountId.deleteAccountId()
    }

    override fun getJwtToken(): String? {
        return local.getJwtToken()
    }

    override suspend fun checkJwtToken(): CheckJwtTokenApiResponses {
        val jwtToken = getJwtToken() ?: return CheckJwtTokenApiResponses.Success(false)
        return remote.checkJwtToken(jwtToken)
    }

    override fun updateJwtToken(newJwtToken: String) {
        return local.updateJwtToken(newJwtToken)
    }
}