package com.kroune.nine_mens_morris_kmp_app.interactors.accountInfo

import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.accountInfoRepository

class AccountInfoInteractorImpl : AccountInfoInteractorI {
    private val remote = accountInfoRepository

    override suspend fun getAccountRatingById(id: Long): Result<Long> {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountRatingById(id, jwtToken)
    }

    override suspend fun getAccountCreationDateById(id: Long): Result<Triple<Int, Int, Int>> {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountCreationDateById(id, jwtToken)
    }

    override suspend fun getAccountLoginById(id: Long): Result<String> {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountLoginById(id, jwtToken)
    }

    override suspend fun getAccountPictureById(id: Long): Result<ByteArray> {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountPictureById(id, jwtToken)
    }

    override suspend fun getOwnAccountId(): Result<Long> {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountIdByJwtToken(jwtToken)
    }
}