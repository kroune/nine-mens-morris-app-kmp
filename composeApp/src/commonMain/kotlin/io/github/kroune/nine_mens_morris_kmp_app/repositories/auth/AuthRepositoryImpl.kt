package io.github.kroune.nine_mens_morris_kmp_app.repositories.auth

import io.github.kroune.nine_mens_morris_kmp_app.data.local.accountId.AccountIdLocalDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.auth.AuthRemoteDataSourceI
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.repositories.jwtToken.JwtTokenRepositoryI

class AuthRepositoryImpl(
    private val accountIdLocalDataSource: AccountIdLocalDataSourceI,
    private val authRemoteDataSource: AuthRemoteDataSourceI,
    private val jwtTokenRepository: JwtTokenRepositoryI
) : AuthRepositoryI {

    override suspend fun login(
        login: String,
        password: String
    ): LoginApiResponse {
        return authRemoteDataSource.login(login, password).also {
            if (it !is LoginApiResponse.Success)
                return@also
            accountIdLocalDataSource.deleteAccountId()
            jwtTokenRepository.updateJwtToken(it.jwtToken)
        }
    }

    override suspend fun register(
        login: String,
        password: String
    ): RegisterApiResponses {
        return authRemoteDataSource.register(login, password).also {
            if (it !is RegisterApiResponses.Success)
                return@also
            accountIdLocalDataSource.deleteAccountId()
            jwtTokenRepository.updateJwtToken(it.jwtToken)
        }
    }

    override fun loginValidator(login: String): Boolean {
        val length = login.length in 5..12
        val content = login.all { it.isLetterOrDigit() }
        return length && content
    }

    override fun passwordValidator(password: String): Boolean {
        val length = password.length in 6..14
        val validString = password.all { it.isLetterOrDigit() }
        val anyDigits = password.any { it.isDigit() }
        val anyLetters = password.any { it.isLetter() }
        return length && validString && anyDigits && anyLetters
    }
}