package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI

class AccountInfoRepositoryImpl(
    private val accountIdLocalDataSource: AccountIdLocalDataSourceI,
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
        // cache
        val localAccountId = accountIdLocalDataSource.getAccountId()
        if (localAccountId != null) {
            return AccountIdByJwtTokenApiResponses.Success(localAccountId)
        }
        // request
        val jwtToken =
            jwtTokenRepository.getJwtToken() ?: error("we have not logged into any account")
        val remoteAccountId = accountInfoRemoteDataSource.getAccountIdByJwtToken(jwtToken)
        if (remoteAccountId is AccountIdByJwtTokenApiResponses.Success)
            accountIdLocalDataSource.updateAccountId(remoteAccountId.accountId)
        return remoteAccountId

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