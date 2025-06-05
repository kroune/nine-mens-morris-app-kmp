package io.github.kroune.nine_mens_morris_kmp_app.interactors.auth

import io.github.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import io.github.kroune.nine_mens_morris_kmp_app.data.accountIdDataSource
import io.github.kroune.nine_mens_morris_kmp_app.data.authRepository
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses

class AuthInteractorImpl : AuthInteractorI {
    private val authRemote = authRepository

    override suspend fun login(
        login: String,
        password: String
    ): LoginApiResponse {
        return authRemote.login(login, password).also {
            if (it !is LoginApiResponse.Success)
                return@also
            accountIdDataSource.deleteAccountId()
            jwtTokenInteractor.updateJwtToken(it.jwtToken)
        }
    }

    override suspend fun register(
        login: String,
        password: String
    ): RegisterApiResponses {
        return authRemote.register(login, password).also {
            if (it !is RegisterApiResponses.Success)
                return@also
            accountIdDataSource.deleteAccountId()
            jwtTokenInteractor.updateJwtToken(it.jwtToken)
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