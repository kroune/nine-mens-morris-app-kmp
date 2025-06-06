package io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryI

class AccountInfoRepositoryImpl(
    private val accountInfoRemoteDataSource: AccountInfoRemoteDataSourceI,
    private val jwtTokenRepository: JwtTokenRepositoryI,
) : AccountInfoRepositoryI {

    override suspend fun getAccountRatingById(id: Long): RatingByIdApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getAccountRatingById(id, jwtToken)
    }

    override suspend fun getAccountCreationDateById(id: Long): CreationDateByIdApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getAccountCreationDateById(id, jwtToken)
    }

    override suspend fun getAccountLoginById(id: Long): LoginByIdApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getAccountLoginById(id, jwtToken)
    }

    override suspend fun getAccountPictureById(id: Long): AccountPictureByIdApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getAccountPictureById(id, jwtToken)
    }

    override suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getAccountIdByJwtToken(jwtToken)
    }

    override suspend fun getLeaderboard(amount: Int): LeaderboardApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.getLeaderboard(amount, jwtToken)
    }

    override suspend fun uploadPicture(picture: ByteArray): UploadPictureApiResponses {
        val jwtToken = jwtTokenRepository.getJwtToken()!!
        return accountInfoRemoteDataSource.uploadPicture(picture, jwtToken)
    }
}