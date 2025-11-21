package io.github.kroune.unitTests.usecases

import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountInfo.AccountInfoRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.useCases.AccountInfoUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AccountInfoUseCaseTest {

    private class MockAccountInfoRepository(
        private val login: LoginByIdApiResponses = LoginByIdApiResponses.Success("testuser"),
        private val rating: RatingByIdApiResponses = RatingByIdApiResponses.Success(1500),
        private val creationDate: CreationDateByIdApiResponses = CreationDateByIdApiResponses.Success(Triple(2024, 1, 1)),
        private val picture: AccountPictureByIdApiResponses = AccountPictureByIdApiResponses.Success(byteArrayOf())
    ) : AccountInfoRepositoryI {
        override suspend fun getAccountLoginById(id: Long) = login
        override suspend fun getAccountRatingById(id: Long) = rating
        override suspend fun getAccountCreationDateById(id: Long) = creationDate
        override suspend fun getAccountPictureById(id: Long) = picture
        override suspend fun getOwnAccountId() = TODO()
        override suspend fun getLeaderboard(amount: Int) = TODO()
        override suspend fun uploadPicture(picture: ByteArray) = TODO()
    }

    @Test
    fun init_triggers_all_callbacks_that_are_provided() = runTest {
        var loginResult: LoginByIdApiResponses? = null
        var ratingResult: RatingByIdApiResponses? = null
        var creationDateResult: CreationDateByIdApiResponses? = null
        var pictureResult: AccountPictureByIdApiResponses? = null

        AccountInfoUseCase(
            accountId = 123L,
            onLoginResult = { loginResult = it },
            onRatingResult = { ratingResult = it },
            needCreationDate = { creationDateResult = it },
            needPicture = { pictureResult = it },
            scope = this,
            accountInfoRepository = MockAccountInfoRepository()
        )

        advanceUntilIdle()

        assertEquals("testuser", (loginResult as? LoginByIdApiResponses.Success)?.login)
        assertEquals(1500, (ratingResult as? RatingByIdApiResponses.Success)?.rating)
        assertEquals(
            Triple(2024, 1, 1),
            (creationDateResult as? CreationDateByIdApiResponses.Success)?.creationDate
        )
        assertEquals(0, (pictureResult as? AccountPictureByIdApiResponses.Success)?.picture?.size)
    }

    @Test
    fun init_does_not_trigger_callbacks_that_are_null() = runTest {
        var loginResult: LoginByIdApiResponses? = null

        AccountInfoUseCase(
            accountId = 123L,
            onLoginResult = { loginResult = it },
            onRatingResult = null,
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository()
        )

        advanceUntilIdle()

        assertEquals("testuser", (loginResult as? LoginByIdApiResponses.Success)?.login)
    }

    @Test
    fun reloadName_updates_login_result() = runTest {
        var loginResult: LoginByIdApiResponses? = null

        val useCase = AccountInfoUseCase(
            accountId = 456L,
            onLoginResult = { loginResult = it },
            onRatingResult = null,
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                login = LoginByIdApiResponses.Success("newuser")
            )
        )

        advanceUntilIdle()
        assertEquals("newuser", (loginResult as? LoginByIdApiResponses.Success)?.login)

        // Reset and reload
        loginResult = null
        useCase.reloadName()
        advanceUntilIdle()

        assertEquals("newuser", (loginResult as? LoginByIdApiResponses.Success)?.login)
    }

    @Test
    fun reloadRating_updates_rating_result() = runTest {
        var ratingResult: RatingByIdApiResponses? = null

        val useCase = AccountInfoUseCase(
            accountId = 789L,
            onLoginResult = null,
            onRatingResult = { ratingResult = it },
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                rating = RatingByIdApiResponses.Success(2000)
            )
        )

        advanceUntilIdle()
        assertEquals(2000, (ratingResult as? RatingByIdApiResponses.Success)?.rating)

        ratingResult = null
        useCase.reloadRating()
        advanceUntilIdle()

        assertEquals(2000, (ratingResult as? RatingByIdApiResponses.Success)?.rating)
    }

    @Test
    fun reloadCreationDate_updates_creation_date_result() = runTest {
        var creationDateResult: CreationDateByIdApiResponses? = null

        val useCase = AccountInfoUseCase(
            accountId = 111L,
            onLoginResult = null,
            onRatingResult = null,
            needCreationDate = { creationDateResult = it },
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                creationDate = CreationDateByIdApiResponses.Success(Triple(2024, 5, 15))
            )
        )

        advanceUntilIdle()
        assertEquals(
            Triple(2024, 5, 15),
            (creationDateResult as? CreationDateByIdApiResponses.Success)?.creationDate
        )

        creationDateResult = null
        useCase.reloadCreationDate()
        advanceUntilIdle()

        assertEquals(
            Triple(2024, 5, 15),
            (creationDateResult as? CreationDateByIdApiResponses.Success)?.creationDate
        )
    }

    @Test
    fun reloadPicture_updates_picture_result() = runTest {
        var pictureResult: AccountPictureByIdApiResponses? = null
        val pictureData = byteArrayOf(1, 2, 3, 4, 5)

        val useCase = AccountInfoUseCase(
            accountId = 222L,
            onLoginResult = null,
            onRatingResult = null,
            needCreationDate = null,
            needPicture = { pictureResult = it },
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                picture = AccountPictureByIdApiResponses.Success(pictureData)
            )
        )

        advanceUntilIdle()
        assertEquals(
            pictureData.toList(),
            (pictureResult as? AccountPictureByIdApiResponses.Success)?.picture?.toList()
        )

        pictureResult = null
        useCase.reloadPicture()
        advanceUntilIdle()

        assertEquals(
            pictureData.toList(),
            (pictureResult as? AccountPictureByIdApiResponses.Success)?.picture?.toList()
        )
    }

    @Test
    fun reload_methods_do_nothing_when_callback_is_null() = runTest {
        val useCase = AccountInfoUseCase(
            accountId = 333L,
            onLoginResult = null,
            onRatingResult = null,
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository()
        )

        advanceUntilIdle()

        // These should not throw exceptions
        useCase.reloadName()
        useCase.reloadRating()
        useCase.reloadCreationDate()
        useCase.reloadPicture()

        advanceUntilIdle()
    }

    @Test
    fun handles_network_errors_in_responses() = runTest {
        var loginResult: LoginByIdApiResponses? = null
        var ratingResult: RatingByIdApiResponses? = null

        AccountInfoUseCase(
            accountId = 444L,
            onLoginResult = { loginResult = it },
            onRatingResult = { ratingResult = it },
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                login = LoginByIdApiResponses.NetworkError(),
                rating = RatingByIdApiResponses.NetworkError()
            )
        )

        advanceUntilIdle()

        assertEquals(true, loginResult is LoginByIdApiResponses.NetworkError)
        assertEquals(true, ratingResult is RatingByIdApiResponses.NetworkError)
    }

    @Test
    fun handles_server_errors_in_responses() = runTest {
        var creationDateResult: CreationDateByIdApiResponses? = null

        AccountInfoUseCase(
            accountId = 555L,
            onLoginResult = null,
            onRatingResult = null,
            needCreationDate = { creationDateResult = it },
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                creationDate = CreationDateByIdApiResponses.ServerError()
            )
        )

        advanceUntilIdle()

        assertEquals(true, creationDateResult is CreationDateByIdApiResponses.ServerError)
    }

    @Test
    fun multiple_reloads_work_correctly() = runTest {
        var callCount = 0

        val useCase = AccountInfoUseCase(
            accountId = 666L,
            onLoginResult = {
                callCount++
            },
            onRatingResult = null,
            needCreationDate = null,
            needPicture = null,
            scope = this,
            accountInfoRepository = MockAccountInfoRepository(
                login = LoginByIdApiResponses.Success("user")
            )
        )

        advanceUntilIdle()
        assertEquals(1, callCount)  // From init

        useCase.reloadName()
        advanceUntilIdle()
        assertEquals(2, callCount)

        useCase.reloadName()
        advanceUntilIdle()
        assertEquals(3, callCount)
    }
}
