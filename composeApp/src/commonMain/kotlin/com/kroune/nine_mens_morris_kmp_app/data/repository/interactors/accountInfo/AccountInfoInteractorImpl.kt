package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfo

import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenRepositoryInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.accountInfoRepository

class AccountInfoInteractorImpl : AccountInfoInteractorI {
    private val remote = accountInfoRepository

    override suspend fun getAccountRatingById(id: Long): Result<Long> {
        val jwtToken = jwtTokenRepositoryInteractor.getJwtToken()!!
        return remote.getAccountRatingById(id, jwtToken)
    }

    override suspend fun getAccountCreationDateById(id: Long): Result<Triple<Int, Int, Int>> {
        val jwtToken = jwtTokenRepositoryInteractor.getJwtToken()!!
        return remote.getAccountCreationDateById(id, jwtToken)
    }

    override suspend fun getAccountLoginById(id: Long): Result<String> {
        val jwtToken = jwtTokenRepositoryInteractor.getJwtToken()!!
        return remote.getAccountLoginById(id, jwtToken)
    }

    override suspend fun getAccountPictureById(id: Long): Result<ByteArray> {
        val jwtToken = jwtTokenRepositoryInteractor.getJwtToken()!!
        return remote.getAccountPictureById(id, jwtToken)
    }

    override suspend fun getOwnAccountId(): Result<Long> {
        val jwtToken = jwtTokenRepositoryInteractor.getJwtToken()!!
        return remote.getAccountIdByJwtToken(jwtToken)
    }
}