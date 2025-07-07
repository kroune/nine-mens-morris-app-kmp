package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses

interface AccountInfoRepositoryI {
    suspend fun getAccountRatingById(id: Long): RatingByIdApiResponses
    suspend fun getAccountCreationDateById(id: Long): CreationDateByIdApiResponses
    suspend fun getAccountLoginById(id: Long): LoginByIdApiResponses
    suspend fun getAccountPictureById(id: Long): AccountPictureByIdApiResponses<ByteArray>
    suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses
    suspend fun getLeaderboard(amount: Int): LeaderboardApiResponses
    suspend fun uploadPicture(picture: ByteArray): UploadPictureApiResponses
}