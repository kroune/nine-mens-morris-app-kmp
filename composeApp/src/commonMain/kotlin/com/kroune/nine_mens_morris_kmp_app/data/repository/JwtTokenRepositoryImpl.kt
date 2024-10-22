package com.kroune.nine_mens_morris_kmp_app.data.repository

import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.JwtTokenDataSourceI
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.local.JwtTokenDataSourceImpl

class JwtTokenRepositoryImpl : JwtTokenRepositoryI {
    private val local: JwtTokenDataSourceI = JwtTokenDataSourceImpl()

    override fun getJwtToken(): String? {
        return local.getJwtToken()
    }

    override fun updateJwtToken(newJwtToken: String) {
        return local.updateJwtToken(newJwtToken)
    }
}