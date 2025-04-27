package io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.UploadPictureApiResponses

interface AccountInfoRepositoryI {
    suspend fun getAccountRatingById(id: Long, jwtToken: String): RatingByIdApiResponses
    suspend fun getAccountCreationDateById(id: Long, jwtToken: String): CreationDateByIdApiResponses
    suspend fun getAccountLoginById(id: Long, jwtToken: String): LoginByIdApiResponses
    suspend fun getAccountPictureById(id: Long, jwtToken: String): AccountPictureByIdApiResponses
    suspend fun getAccountIdByJwtToken(jwtToken: String): AccountIdByJwtTokenApiResponses
    suspend fun getLeaderboard(amount: Int, jwtToken: String): LeaderboardApiResponses
    suspend fun uploadPicture(picture: ByteArray, jwtToken: String): UploadPictureApiResponses
}