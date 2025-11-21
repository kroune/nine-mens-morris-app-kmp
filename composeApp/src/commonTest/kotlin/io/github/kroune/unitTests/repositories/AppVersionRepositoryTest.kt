package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.data.remote.appVersion.AppVersionRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AppLastVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RequiredVersionApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.appVersion.AppVersionRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppVersionRepositoryTest {

    private class MockAppVersionRemoteDataSource(
        private val lastVersionResponse: AppLastVersionApiResponse,
        private val requiredVersionResponse: RequiredVersionApiResponse
    ) : AppVersionRemoteDataSourceI {
        override suspend fun getLastAppVersion() = lastVersionResponse
        override suspend fun getRequiredVersion() = requiredVersionResponse
    }

    @Test
    fun getLastAppVersionReturnsVersionFromRemote() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.Success(150),
                requiredVersionResponse = RequiredVersionApiResponse.Success(100)
            )
        )

        val result = repository.getLastAppVersion()
        assertTrue(result is AppLastVersionApiResponse.Success)
        assertEquals(150, result.lastVersion)
    }

    @Test
    fun getLastAppVersionHandlesNetworkError() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.NetworkError(),
                requiredVersionResponse = RequiredVersionApiResponse.Success(100)
            )
        )

        val result = repository.getLastAppVersion()
        assertTrue(result is AppLastVersionApiResponse.NetworkError)
    }

    @Test
    fun getLastAppVersionHandlesServerError() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.ServerError(),
                requiredVersionResponse = RequiredVersionApiResponse.Success(100)
            )
        )

        val result = repository.getLastAppVersion()
        assertTrue(result is AppLastVersionApiResponse.ServerError)
    }

    @Test
    fun getRequiredVersionReturnsVersionFromRemote() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.Success(150),
                requiredVersionResponse = RequiredVersionApiResponse.Success(120)
            )
        )

        val result = repository.getRequiredVersion()
        assertTrue(result is RequiredVersionApiResponse.Success)
        assertEquals(120, result.requiredVersion)
    }

    @Test
    fun getRequiredVersionHandlesNetworkError() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.Success(150),
                requiredVersionResponse = RequiredVersionApiResponse.NetworkError()
            )
        )

        val result = repository.getRequiredVersion()
        assertTrue(result is RequiredVersionApiResponse.NetworkError)
    }

    @Test
    fun getRequiredVersionHandlesServerError() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.Success(150),
                requiredVersionResponse = RequiredVersionApiResponse.ServerError()
            )
        )

        val result = repository.getRequiredVersion()
        assertTrue(result is RequiredVersionApiResponse.ServerError)
    }

    @Test
    fun multipleCallsReturnConsistentResults() = runTest {
        val repository = AppVersionRepositoryImpl(
            appVersionRemoteDataSource = MockAppVersionRemoteDataSource(
                lastVersionResponse = AppLastVersionApiResponse.Success(200),
                requiredVersionResponse = RequiredVersionApiResponse.Success(180)
            )
        )

        val result1 = repository.getLastAppVersion()
        val result2 = repository.getLastAppVersion()

        assertTrue(result1 is AppLastVersionApiResponse.Success)
        assertTrue(result2 is AppLastVersionApiResponse.Success)
        assertEquals(result1.lastVersion, result2.lastVersion)
    }
}
