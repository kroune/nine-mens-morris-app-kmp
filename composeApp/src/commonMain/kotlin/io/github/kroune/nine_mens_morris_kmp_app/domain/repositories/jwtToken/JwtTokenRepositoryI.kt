package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses

interface JwtTokenRepositoryI {
    fun logout()
    fun getJwtToken(): String?
    suspend fun checkJwtToken(): CheckJwtTokenApiResponses
    fun updateJwtToken(newJwtToken: String)
}
