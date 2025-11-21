package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountId.AccountIdRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AccountIdRepositoryTest {

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

    private class MockAccountInfoRepository(
        private val response: AccountIdByJwtTokenApiResponses
    ) : AccountInfoRepositoryI {
        override suspend fun getOwnAccountId(): AccountIdByJwtTokenApiResponses = response
        override suspend fun getAccountRatingById(id: Long) = TODO()
        override suspend fun getAccountCreationDateById(id: Long) = TODO()
        override suspend fun getAccountLoginById(id: Long) = TODO()
        override suspend fun getAccountPictureById(id: Long) = TODO()
        override suspend fun getLeaderboard(amount: Int) = TODO()
        override suspend fun uploadPicture(picture: ByteArray) = TODO()
    }

    private class MockJwtTokenRepository(
        private val token: String?
    ) : JwtTokenRepositoryI {
        override fun logout() {}
        override fun getJwtToken(): String? = token
        override suspend fun checkJwtToken() = TODO()
        override fun updateJwtToken(newJwtToken: String) {}
    }

    @Test
    fun `getAccountId returns local account id when available`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()
        localDataSource.updateAccountId(12345L)

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.Success(67890L)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.Success)
        assertEquals(12345L, result.accountId)
    }

    @Test
    fun `getAccountId fetches from remote when local is null and jwt token exists`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()
        val remoteAccountId = 67890L

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.Success(remoteAccountId)
            ),
            jwtTokenRepository = MockJwtTokenRepository("valid-token")
        )

        val result = repository.getAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.Success)
        assertEquals(remoteAccountId, result.accountId)
        // Check that local storage was updated
        assertEquals(remoteAccountId, localDataSource.getAccountId())
    }

    @Test
    fun `getAccountId returns error response when remote fetch fails`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.NetworkError()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.NetworkError)
    }

    @Test
    fun `getAccountId throws error when no local id and no jwt token`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.Success(123L)
            ),
            jwtTokenRepository = MockJwtTokenRepository(null)
        )

        assertFailsWith<IllegalStateException> {
            repository.getAccountId()
        }
    }

    @Test
    fun `updateAccountId updates local storage`() {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.Success(123L)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        repository.updateAccountId(99999L)
        assertEquals(99999L, localDataSource.getAccountId())
    }

    @Test
    fun `getAccountId handles CredentialsError from remote`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.CredentialsError()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.CredentialsError)
    }

    @Test
    fun `getAccountId handles ServerError from remote`() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountIdRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRepository = MockAccountInfoRepository(
                AccountIdByJwtTokenApiResponses.ServerError()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.ServerError)
    }
}
