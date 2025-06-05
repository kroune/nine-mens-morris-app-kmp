package io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth

import io.github.kroune.nine_mens_morris_kmp_app.model.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses

interface AuthRepositoryI {
    suspend fun checkJwtToken(jwtToken: String): CheckJwtTokenApiResponses
    suspend fun login(login: String, password: String): LoginApiResponse
    suspend fun register(login: String, password: String): RegisterApiResponses
}