package com.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo

interface AccountInfoRepositoryI {
    suspend fun getAccountRatingById(id: Long, jwtToken: String): Result<Long>
    suspend fun getAccountCreationDateById(id: Long, jwtToken: String): Result<Triple<Int, Int, Int>>
    suspend fun getAccountLoginById(id: Long, jwtToken: String): Result<String>
    suspend fun getAccountPictureById(id: Long, jwtToken: String): Result<ByteArray>
    suspend fun getAccountIdByJwtToken(jwtToken: String): Result<Long>
    suspend fun getLeaderboard(jwtToken: String): Result<List<Long>>
    suspend fun uploadPicture(picture: ByteArray, jwtToken: String): Result<Unit>
}