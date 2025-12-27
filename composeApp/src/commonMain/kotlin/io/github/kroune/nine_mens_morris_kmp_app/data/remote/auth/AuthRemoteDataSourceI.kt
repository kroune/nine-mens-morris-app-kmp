package io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RegisterApiResponses

interface AuthRemoteDataSourceI {
    suspend fun checkJwtToken(jwtToken: String): CheckJwtTokenApiResponses
    suspend fun login(login: String, password: String): LoginApiResponse
    suspend fun register(login: String, password: String): RegisterApiResponses
}
