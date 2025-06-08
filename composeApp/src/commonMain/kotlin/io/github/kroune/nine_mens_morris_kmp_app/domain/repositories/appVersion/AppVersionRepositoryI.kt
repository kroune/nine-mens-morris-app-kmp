package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.appVersion

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse

interface AppVersionRepositoryI {
    suspend fun getLastAppVersion(): AppLastVersionApiResponse
    suspend fun getRequiredVersion(): RequiredVersionApiResponse
}