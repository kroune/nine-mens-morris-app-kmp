package com.kroune.nine_mens_morris_kmp_app.data.repository.source.local

interface JwtTokenDataSourceI {
    fun getJwtToken(): String?
    fun updateJwtToken(newJwtToken: String)
}