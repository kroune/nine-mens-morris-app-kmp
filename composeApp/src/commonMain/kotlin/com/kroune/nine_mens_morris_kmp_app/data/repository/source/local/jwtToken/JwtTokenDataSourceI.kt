package com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.jwtToken

interface JwtTokenDataSourceI {
    fun getJwtToken(): String?
    fun deleteJwtToken()
    fun updateJwtToken(newJwtToken: String)
}