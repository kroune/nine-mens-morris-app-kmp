package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.local.jwtToken.JwtTokenRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CheckJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JwtTokenRepositoryTest {

    private class MockJwtTokenLocalDataSource : JwtTokenRemoteDataSourceI {
        private var token: String? = null

        override fun getJwtToken(): String? = token
        override fun updateJwtToken(newJwtToken: String) {
            token = newJwtToken
        }
        override fun deleteJwtToken() {
            token = null
        }
    }

    private class MockAccountIdLocalDataSource : AccountIdLocalDataSourceI {
        private var accountId: Long? = null

        override fun getAccountId(): Long? = accountId
        override fun updateAccountId(newAccountId: Long) {
            accountId = newAccountId
        }
        override fun deleteAccountId() {
            accountId = null
        }
    }

    private class MockAuthRemoteDataSource(
        private val checkTokenResponse: CheckJwtTokenApiResponses
    ) : AuthRemoteDataSourceI {
        override suspend fun checkJwtToken(jwtToken: String) = checkTokenResponse
        override suspend fun login(login: String, password: String) = TODO()
        override suspend fun register(login: String, password: String) = TODO()
    }

    @Test
    fun `getJwtToken returns stored token`() {
        val localDataSource = MockJwtTokenLocalDataSource()
        localDataSource.updateJwtToken("test-token")

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(CheckJwtTokenApiResponses.Success(true)),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        assertEquals("test-token", repository.getJwtToken())
    }

    @Test
    fun `getJwtToken returns null when no token is stored`() {
        val repository = JwtTokenRepositoryImpl(
            local = MockJwtTokenLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(CheckJwtTokenApiResponses.Success(true)),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        assertNull(repository.getJwtToken())
    }

    @Test
    fun `updateJwtToken updates stored token`() {
        val localDataSource = MockJwtTokenLocalDataSource()

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(CheckJwtTokenApiResponses.Success(true)),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        repository.updateJwtToken("new-token")
        assertEquals("new-token", localDataSource.getJwtToken())
    }

    @Test
    fun `logout deletes both token and account id`() {
        val localDataSource = MockJwtTokenLocalDataSource()
        localDataSource.updateJwtToken("token")

        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(123L)

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(CheckJwtTokenApiResponses.Success(true)),
            accountIdLocalDataSource = accountIdDataSource
        )

        repository.logout()

        assertNull(localDataSource.getJwtToken())
        assertNull(accountIdDataSource.getAccountId())
    }

    @Test
    fun `checkJwtToken returns success when token is valid`() = runTest {
        val localDataSource = MockJwtTokenLocalDataSource()
        localDataSource.updateJwtToken("valid-token")

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                CheckJwtTokenApiResponses.Success(true)
            ),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        val result = repository.checkJwtToken()
        assertTrue(result is CheckJwtTokenApiResponses.Success)
        assertEquals(true, result.result)
    }

    @Test
    fun `checkJwtToken returns success false when no token exists`() = runTest {
        val repository = JwtTokenRepositoryImpl(
            local = MockJwtTokenLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                CheckJwtTokenApiResponses.Success(true)
            ),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        val result = repository.checkJwtToken()
        assertTrue(result is CheckJwtTokenApiResponses.Success)
        assertEquals(false, result.result)
    }

    @Test
    fun `checkJwtToken handles network errors`() = runTest {
        val localDataSource = MockJwtTokenLocalDataSource()
        localDataSource.updateJwtToken("token")

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                CheckJwtTokenApiResponses.NetworkError()
            ),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        val result = repository.checkJwtToken()
        assertTrue(result is CheckJwtTokenApiResponses.NetworkError)
    }

    @Test
    fun `checkJwtToken handles server errors`() = runTest {
        val localDataSource = MockJwtTokenLocalDataSource()
        localDataSource.updateJwtToken("token")

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                CheckJwtTokenApiResponses.ServerError()
            ),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        val result = repository.checkJwtToken()
        assertTrue(result is CheckJwtTokenApiResponses.ServerError)
    }

    @Test
    fun `multiple updateJwtToken calls update correctly`() {
        val localDataSource = MockJwtTokenLocalDataSource()

        val repository = JwtTokenRepositoryImpl(
            local = localDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(CheckJwtTokenApiResponses.Success(true)),
            accountIdLocalDataSource = MockAccountIdLocalDataSource()
        )

        repository.updateJwtToken("token1")
        assertEquals("token1", repository.getJwtToken())

        repository.updateJwtToken("token2")
        assertEquals("token2", repository.getJwtToken())

        repository.updateJwtToken("token3")
        assertEquals("token3", repository.getJwtToken())
    }
}
