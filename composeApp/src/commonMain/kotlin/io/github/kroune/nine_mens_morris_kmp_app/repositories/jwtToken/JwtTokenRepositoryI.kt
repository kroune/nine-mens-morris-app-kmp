package io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken

import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses

interface JwtTokenRepositoryI {
    fun logout()
    fun getJwtToken(): String?
    suspend fun checkJwtToken(): CheckJwtTokenApiResponses
    fun updateJwtToken(newJwtToken: String)
}