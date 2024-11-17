package com.kroune.nine_mens_morris_kmp_app.interactors.jwtToken

interface JwtTokenInteractorI {
    fun logout()
    fun getJwtToken(): String?
    suspend fun checkJwtToken(): Result<Boolean>
    fun updateJwtToken(newJwtToken: String)
}