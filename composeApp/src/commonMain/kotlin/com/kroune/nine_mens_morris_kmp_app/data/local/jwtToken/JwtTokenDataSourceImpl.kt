package com.kroune.nine_mens_morris_kmp_app.data.local.jwtToken

import com.russhwolf.settings.Settings

class JwtTokenDataSourceImpl: JwtTokenDataSourceI {
    override fun getJwtToken(): String? {
        return Settings().getStringOrNull("jwtToken")
    }

    override fun deleteJwtToken() {
        Settings().remove("jwtToken")
    }

    override fun updateJwtToken(newJwtToken: String) {
        Settings().putString("jwtToken", newJwtToken)
    }
}