package com.kroune.nine_mens_morris_kmp_app.data.repository.source.local

import com.russhwolf.settings.Settings

class JwtTokenDataSourceImpl: JwtTokenDataSourceI {
    override fun getJwtToken(): String? {
        return Settings().getStringOrNull("jwtToken")
    }

    override fun updateJwtToken(newJwtToken: String) {
        Settings().putString("jwtToken", newJwtToken)
    }
}