package io.github.kroune.unitTests.repositories

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.auth.AuthRepositoryImpl
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.jwtToken.JwtTokenRepositoryI
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

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

    private class MockJwtTokenRepository : JwtTokenRepositoryI {
        private var token: String? = null

        override fun logout() {}
        override fun getJwtToken(): String? = token
        override suspend fun checkJwtToken() = TODO()
        override fun updateJwtToken(newJwtToken: String) {
            token = newJwtToken
        }
    }

    private class MockAuthRemoteDataSource(
        private val loginResponse: LoginApiResponse,
        private val registerResponse: RegisterApiResponses
    ) : AuthRemoteDataSourceI {
        override suspend fun login(login: String, password: String) = loginResponse
        override suspend fun register(login: String, password: String) = registerResponse
        override suspend fun checkJwtToken(jwtToken: String) = TODO()
    }

    @Test
    fun `login with valid credentials returns success and updates token`() = runTest {
        val jwtTokenRepo = MockJwtTokenRepository()
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(123L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("jwt-token-123"),
                registerResponse = RegisterApiResponses.Success("jwt-token-123")
            ),
            jwtTokenRepository = jwtTokenRepo
        )

        val result = repository.login("testuser", "password1")
        assertTrue(result is LoginApiResponse.Success)
        assertEquals("jwt-token-123", result.jwtToken)
        assertEquals("jwt-token-123", jwtTokenRepo.getJwtToken())
        assertNull(accountIdDataSource.getAccountId())
    }

    @Test
    fun `login with invalid credentials returns CredentialsError`() = runTest {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.CredentialsError(),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        val result = repository.login("invalid", "wrong")
        assertTrue(result is LoginApiResponse.CredentialsError)
    }

    @Test
    fun `login with network error returns NetworkError`() = runTest {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.NetworkError(),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        val result = repository.login("user", "pass")
        assertTrue(result is LoginApiResponse.NetworkError)
    }

    @Test
    fun `register with valid data returns success and updates token`() = runTest {
        val jwtTokenRepo = MockJwtTokenRepository()
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(456L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("register-token-456")
            ),
            jwtTokenRepository = jwtTokenRepo
        )

        val result = repository.register("newuser", "password1")
        assertTrue(result is RegisterApiResponses.Success)
        assertEquals("register-token-456", result.jwtToken)
        assertEquals("register-token-456", jwtTokenRepo.getJwtToken())
        assertNull(accountIdDataSource.getAccountId())
    }

    @Test
    fun `register with existing login returns LoginAlreadyInUse`() = runTest {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.LoginAlreadyInUse()
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        val result = repository.register("existinguser", "password1")
        assertTrue(result is RegisterApiResponses.LoginAlreadyInUse)
    }

    @Test
    fun `loginValidator accepts valid logins`() {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        assertTrue(repository.loginValidator("user12"))  // 5 chars
        assertTrue(repository.loginValidator("testuser"))  // 8 chars
        assertTrue(repository.loginValidator("longusername"))  // 12 chars
        assertTrue(repository.loginValidator("abc123"))  // mixed
    }

    @Test
    fun `loginValidator rejects invalid logins`() {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        assertFalse(repository.loginValidator("usr"))  // too short (< 5)
        assertFalse(repository.loginValidator("verylongusername"))  // too long (> 12)
        assertFalse(repository.loginValidator("user-123"))  // contains hyphen
        assertFalse(repository.loginValidator("user 123"))  // contains space
        assertFalse(repository.loginValidator("user@123"))  // contains @
    }

    @Test
    fun `passwordValidator accepts valid passwords`() {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        assertTrue(repository.passwordValidator("pass12"))  // 6 chars, mixed
        assertTrue(repository.passwordValidator("password1"))  // 9 chars, mixed
        assertTrue(repository.passwordValidator("12345password"))  // 13 chars, mixed
        assertTrue(repository.passwordValidator("abc123"))  // 6 chars, mixed
        assertTrue(repository.passwordValidator("test1234567890"))  // 14 chars, mixed
    }

    @Test
    fun `passwordValidator rejects invalid passwords`() {
        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = MockAccountIdLocalDataSource(),
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        assertFalse(repository.passwordValidator("pass1"))  // too short (< 6)
        assertFalse(repository.passwordValidator("verylongpassword123"))  // too long (> 14)
        assertFalse(repository.passwordValidator("password"))  // no digits
        assertFalse(repository.passwordValidator("12345678"))  // no letters
        assertFalse(repository.passwordValidator("pass-123"))  // contains special char
        assertFalse(repository.passwordValidator("pass 123"))  // contains space
    }

    @Test
    fun `login clears account id on success`() = runTest {
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(999L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("new-token"),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        repository.login("user", "pass1")
        assertNull(accountIdDataSource.getAccountId())
    }

    @Test
    fun `register clears account id on success`() = runTest {
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(888L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.Success("new-token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        repository.register("newuser", "pass1")
        assertNull(accountIdDataSource.getAccountId())
    }

    @Test
    fun `login does not clear account id on error`() = runTest {
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(777L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.NetworkError(),
                registerResponse = RegisterApiResponses.Success("token")
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        repository.login("user", "pass")
        assertEquals(777L, accountIdDataSource.getAccountId())
    }

    @Test
    fun `register does not clear account id on error`() = runTest {
        val accountIdDataSource = MockAccountIdLocalDataSource()
        accountIdDataSource.updateAccountId(666L)

        val repository = AuthRepositoryImpl(
            accountIdLocalDataSource = accountIdDataSource,
            authRemoteDataSource = MockAuthRemoteDataSource(
                loginResponse = LoginApiResponse.Success("token"),
                registerResponse = RegisterApiResponses.NetworkError()
            ),
            jwtTokenRepository = MockJwtTokenRepository()
        )

        repository.register("user", "pass")
        assertEquals(666L, accountIdDataSource.getAccountId())
    }
}
