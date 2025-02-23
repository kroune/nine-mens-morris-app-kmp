package io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtToken

import io.github.kroune.nine_mens_morris_kmp_app.data.accountIdDataSource
import io.github.kroune.nine_mens_morris_kmp_app.data.authRepository
import io.github.kroune.nine_mens_morris_kmp_app.data.jwtTokenDataSource
import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRepositoryI

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

    override suspend fun checkJwtToken(): Result<Boolean> {
        val jwtToken = getJwtToken() ?: return Result.success(false)
        return remote.checkJwtToken(jwtToken)
    }

    override fun updateJwtToken(newJwtToken: String) {
        return local.updateJwtToken(newJwtToken)
    }
}