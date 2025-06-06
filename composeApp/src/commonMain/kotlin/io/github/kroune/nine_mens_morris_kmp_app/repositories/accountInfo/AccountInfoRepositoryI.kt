package io.github.kroune.nine_mens_morris_kmp_app.repositories.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.UploadPictureApiResponses

interface AccountInfoRepositoryI {
    suspend fun getAccountRatingById(id: Long): RatingByIdApiResponses
    suspend fun getAccountCreationDateById(id: Long): CreationDateByIdApiResponses
    suspend fun getAccountLoginById(id: Long): LoginByIdApiResponses
    suspend fun getAccountPictureById(id: Long): AccountPictureByIdApiResponses
    suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses
    suspend fun getLeaderboard(amount: Int): LeaderboardApiResponses
    suspend fun uploadPicture(picture: ByteArray): UploadPictureApiResponses
}