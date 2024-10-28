package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountInfo

interface AccountInfoInteractorI {
    suspend fun getAccountRatingById(id: Long): Result<Long>
    suspend fun getAccountCreationDateById(id: Long): Result<Triple<Int, Int, Int>>
    suspend fun getAccountLoginById(id: Long): Result<String>
    suspend fun getAccountPictureById(id: Long): Result<ByteArray>
    suspend fun getOwnAccountId(): Result<Long>
}