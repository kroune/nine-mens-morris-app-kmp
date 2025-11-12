package io.github.kroune.nine_mens_morris_kmp_app.data.remote.appVersion

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse

interface AppVersionRemoteDataSourceI {
    suspend fun getLastAppVersion(): AppLastVersionApiResponse
    suspend fun getRequiredVersion(): RequiredVersionApiResponse
}
