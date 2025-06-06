package io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken

interface JwtTokenRemoteDataSourceI {
    fun getJwtToken(): String?
    fun deleteJwtToken()
    fun updateJwtToken(newJwtToken: String)
}