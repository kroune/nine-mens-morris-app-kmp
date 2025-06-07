package io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.appVersion

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.appVersion.AppVersionRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse

class AppVersionRepositoryImpl(
    private val appVersionRemoteDataSource: AppVersionRemoteDataSourceI
): AppVersionRepositoryI {

    override suspend fun getLastAppVersion(): AppLastVersionApiResponse {
        return appVersionRemoteDataSource.getLastAppVersion()
    }

    override suspend fun getRequiredVersion(): RequiredVersionApiResponse {
        return appVersionRemoteDataSource.getRequiredVersion()
    }
}