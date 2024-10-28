package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtToken

import com.kroune.nine_mens_morris_kmp_app.data.repository.source.accountIdDataSource
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.authRepository
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.jwtTokenDataSource

class JwtTokenInteractorImpl : JwtTokenInteractorI {
    private val local = jwtTokenDataSource
    private val localAccountIdDataSource = accountIdDataSource
    private val remote = authRepository

    override fun logout() {
        local.deleteJwtToken()
        localAccountIdDataSource.deleteAccountId()
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