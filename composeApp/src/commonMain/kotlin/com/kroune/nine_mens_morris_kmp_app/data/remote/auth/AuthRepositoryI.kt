package com.kroune.nine_mens_morris_kmp_app.data.remote.auth

interface AuthRepositoryI {
    suspend fun checkJwtToken(jwtToken: String): Result<Boolean>
    suspend fun login(login: String, password: String): Result<String>
    suspend fun register(login: String, password: String): Result<String>
}