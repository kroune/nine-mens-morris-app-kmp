package io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.data.accountInfoRepository
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.UploadPictureApiResponses

class AccountInfoInteractorImpl : AccountInfoInteractorI {
    private val remote = accountInfoRepository

    override suspend fun getAccountRatingById(id: Long): RatingByIdApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountRatingById(id, jwtToken)
    }

    override suspend fun getAccountCreationDateById(id: Long): CreationDateByIdApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountCreationDateById(id, jwtToken)
    }

    override suspend fun getAccountLoginById(id: Long): LoginByIdApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountLoginById(id, jwtToken)
    }

    override suspend fun getAccountPictureById(id: Long): AccountPictureByIdApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountPictureById(id, jwtToken)
    }

    override suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getAccountIdByJwtToken(jwtToken)
    }

    override suspend fun getLeaderboard(amount: Int): LeaderboardApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.getLeaderboard(amount, jwtToken)
    }

    override suspend fun uploadPicture(picture: ByteArray): UploadPictureApiResponses {
        val jwtToken = jwtTokenInteractor.getJwtToken()!!
        return remote.uploadPicture(picture, jwtToken)
    }
}