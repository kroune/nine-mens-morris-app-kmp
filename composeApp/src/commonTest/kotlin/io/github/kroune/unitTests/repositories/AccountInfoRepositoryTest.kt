package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.accountInfo.AccountInfoRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.*
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccountInfoRepositoryTest {

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

    private class MockAccountInfoRemoteDataSource(
        private val ratingResponse: RatingByIdApiResponses = RatingByIdApiResponses.Success(1500),
        private val creationDateResponse: CreationDateByIdApiResponses = CreationDateByIdApiResponses.Success(Triple(2024, 1, 1)),
        private val loginResponse: LoginByIdApiResponses = LoginByIdApiResponses.Success("testuser"),
        private val pictureResponse: AccountPictureByIdApiResponses = AccountPictureByIdApiResponses.Success(byteArrayOf()),
        private val accountIdResponse: AccountIdByJwtTokenApiResponses = AccountIdByJwtTokenApiResponses.Success(123L),
        private val leaderboardResponse: LeaderboardApiResponses = LeaderboardApiResponses.Success(emptyList()),
        private val uploadPictureResponse: UploadPictureApiResponses = UploadPictureApiResponses.Success()
    ) : AccountInfoRemoteDataSourceI {
        override suspend fun getAccountRatingById(id: Long, jwtToken: String) = ratingResponse
        override suspend fun getAccountCreationDateById(id: Long, jwtToken: String) = creationDateResponse
        override suspend fun getAccountLoginById(id: Long, jwtToken: String) = loginResponse
        override suspend fun getAccountPictureById(id: Long, jwtToken: String) = pictureResponse
        override suspend fun getAccountIdByJwtToken(jwtToken: String) = accountIdResponse
        override suspend fun getLeaderboard(amount: Int, jwtToken: String) = leaderboardResponse
        override suspend fun uploadPicture(picture: ByteArray, jwtToken: String) = uploadPictureResponse
    }

    private class MockJwtTokenRepository(private val token: String?) : JwtTokenRepositoryI {
        override fun logout() {}
        override fun getJwtToken(): String? = token
        override suspend fun checkJwtToken() = TODO()
        override fun updateJwtToken(newJwtToken: String) {}
    }

    @Test
    fun getAccountRatingByIdReturnsRatingFromRemote() = runTest {
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                ratingResponse = RatingByIdApiResponses.Success(1800)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountRatingById(123L)
        assertTrue(result is RatingByIdApiResponses.Success)
        assertEquals(1800, result.rating)
    }

    @Test
    fun getAccountCreationDateByIdReturnsDateFromRemote() = runTest {
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                creationDateResponse = CreationDateByIdApiResponses.Success(Triple(2024, 5, 15))
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountCreationDateById(123L)
        assertTrue(result is CreationDateByIdApiResponses.Success)
        assertEquals(Triple(2024, 5, 15), result.creationDate)
    }

    @Test
    fun getAccountLoginByIdReturnsLoginFromRemote() = runTest {
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                loginResponse = LoginByIdApiResponses.Success("john_doe")
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountLoginById(123L)
        assertTrue(result is LoginByIdApiResponses.Success)
        assertEquals("john_doe", result.login)
    }

    @Test
    fun getAccountPictureByIdReturnsPictureFromRemote() = runTest {
        val pictureData = byteArrayOf(1, 2, 3, 4, 5)
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                pictureResponse = AccountPictureByIdApiResponses.Success(pictureData)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getAccountPictureById(123L)
        assertTrue(result is AccountPictureByIdApiResponses.Success)
        assertEquals(pictureData.toList(), result.picture.toList())
    }

    @Test
    fun getOwnAccountIdReturnsCachedLocalAccountId() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()
        localDataSource.updateAccountId(456L)

        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                accountIdResponse = AccountIdByJwtTokenApiResponses.Success(789L)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getOwnAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.Success)
        assertEquals(456L, result.accountId)
    }

    @Test
    fun getOwnAccountIdFetchesFromRemoteWhenCacheIsEmpty() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                accountIdResponse = AccountIdByJwtTokenApiResponses.Success(789L)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getOwnAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.Success)
        assertEquals(789L, result.accountId)
        // Verify cache was updated
        assertEquals(789L, localDataSource.getAccountId())
    }

    @Test
    fun getOwnAccountIdDoesNotCacheOnErrorResponse() = runTest {
        val localDataSource = MockAccountIdLocalDataSource()

        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = localDataSource,
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                accountIdResponse = AccountIdByJwtTokenApiResponses.NetworkError()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getOwnAccountId()
        assertTrue(result is AccountIdByJwtTokenApiResponses.NetworkError)
        assertNull(localDataSource.getAccountId())
    }

    @Test
    fun getLeaderboardReturnsLeaderboardFromRemote() = runTest {
        val leaderboardData = listOf(1L, 2L, 3L, 4L, 5L)

        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                leaderboardResponse = LeaderboardApiResponses.Success(leaderboardData)
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val result = repository.getLeaderboard(10)
        assertTrue(result is LeaderboardApiResponses.Success)
        assertEquals(5, result.leaderboard.size)
        assertEquals(1L, result.leaderboard[0])
    }

    @Test
    fun uploadPictureUploadsPictureToRemote() = runTest {
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                uploadPictureResponse = UploadPictureApiResponses.Success()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        val picture = byteArrayOf(1, 2, 3)
        val result = repository.uploadPicture(picture)
        assertTrue(result is UploadPictureApiResponses.Success)
    }

    @Test
    fun methodsHandleNetworkErrors() = runTest {
        val repository = AccountInfoRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            accountInfoRemoteDataSource = MockAccountInfoRemoteDataSource(
                ratingResponse = RatingByIdApiResponses.NetworkError(),
                loginResponse = LoginByIdApiResponses.NetworkError(),
                creationDateResponse = CreationDateByIdApiResponses.NetworkError()
            ),
            jwtTokenRepository = MockJwtTokenRepository("token")
        )

        assertTrue(repository.getAccountRatingById(1L) is RatingByIdApiResponses.NetworkError)
        assertTrue(repository.getAccountLoginById(1L) is LoginByIdApiResponses.NetworkError)
        assertTrue(repository.getAccountCreationDateById(1L) is CreationDateByIdApiResponses.NetworkError)
    }
}
