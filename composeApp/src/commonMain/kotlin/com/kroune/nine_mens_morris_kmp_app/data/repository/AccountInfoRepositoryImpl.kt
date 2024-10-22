package com.kroune.nine_mens_morris_kmp_app.data.repository

import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RemoteAccountInfoDataSource

class AccountInfoRepositoryImpl: AccountInfoRepositoryI {
    private val remote = RemoteAccountInfoDataSource()

    override suspend fun getAccountRatingById(id: Long): Result<Long> {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return remote.getAccountRatingById(id, jwtToken)
    }

    override suspend fun getAccountCreationDateById(id: Long): Result<Triple<Int, Int, Int>> {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return remote.getAccountCreationDateById(id, jwtToken)
    }

    override suspend fun getAccountLoginById(id: Long): Result<String> {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return remote.getAccountLoginById(id, jwtToken)
    }

    override suspend fun getAccountPictureById(id: Long): Result<ByteArray> {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return remote.getAccountPictureById(id, jwtToken)
    }

    override suspend fun getOwnAccountId(): Result<Long> {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return remote.getAccountIdByJwtToken(jwtToken)
    }
}