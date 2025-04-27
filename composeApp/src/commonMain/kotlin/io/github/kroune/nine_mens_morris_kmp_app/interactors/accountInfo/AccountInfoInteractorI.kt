package io.github.kroune.nine_mens_morris_kmp_app.interactors.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.UploadPictureApiResponses

interface AccountInfoInteractorI {
    suspend fun getAccountRatingById(id: Long): RatingByIdApiResponses
    suspend fun getAccountCreationDateById(id: Long): CreationDateByIdApiResponses
    suspend fun getAccountLoginById(id: Long): LoginByIdApiResponses
    suspend fun getAccountPictureById(id: Long): AccountPictureByIdApiResponses
    suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses
    suspend fun getLeaderboard(amount: Int): LeaderboardApiResponses
    suspend fun uploadPicture(picture: ByteArray): UploadPictureApiResponses
}