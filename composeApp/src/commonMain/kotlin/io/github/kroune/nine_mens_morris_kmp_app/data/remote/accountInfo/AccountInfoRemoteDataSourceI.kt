package io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LeaderboardApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses

interface AccountInfoRemoteDataSourceI {
    suspend fun getAccountRatingById(id: Long, jwtToken: String): RatingByIdApiResponses
    suspend fun getAccountCreationDateById(id: Long, jwtToken: String): CreationDateByIdApiResponses
    suspend fun getAccountLoginById(id: Long, jwtToken: String): LoginByIdApiResponses
    suspend fun getAccountPictureById(id: Long, jwtToken: String): AccountPictureByIdApiResponses<ByteArray>
    suspend fun getAccountIdByJwtToken(jwtToken: String): AccountIdByJwtTokenApiResponses
    suspend fun getLeaderboard(amount: Int, jwtToken: String): LeaderboardApiResponses
    suspend fun uploadPicture(picture: ByteArray, jwtToken: String): UploadPictureApiResponses
}