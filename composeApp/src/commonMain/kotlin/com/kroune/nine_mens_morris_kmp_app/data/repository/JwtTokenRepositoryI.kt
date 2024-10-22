package com.kroune.nine_mens_morris_kmp_app.data.repository

interface JwtTokenRepositoryI {
    fun getJwtToken(): String?
    fun updateJwtToken(newJwtToken: String)
}