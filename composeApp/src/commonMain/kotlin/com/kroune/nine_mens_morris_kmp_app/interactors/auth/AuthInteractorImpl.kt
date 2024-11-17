package com.kroune.nine_mens_morris_kmp_app.interactors.auth

import com.kroune.nine_mens_morris_kmp_app.interactors.jwtTokenInteractor
import com.kroune.nine_mens_morris_kmp_app.data.accountIdDataSource
import com.kroune.nine_mens_morris_kmp_app.data.authRepository

class AuthInteractorImpl : AuthInteractorI {
    val authRemote = authRepository

    override suspend fun login(
        login: String,
        password: String
    ): Result<String> {
        val result = authRemote.login(login, password).onSuccess { jwtToken ->
            accountIdDataSource.deleteAccountId()
            jwtTokenInteractor.updateJwtToken(jwtToken)
        }
        return result
    }

    override suspend fun register(
        login: String,
        password: String
    ): Result<String> {
        val result = authRemote.register(login, password).onSuccess { jwtToken ->
            accountIdDataSource.deleteAccountId()
            jwtTokenInteractor.updateJwtToken(jwtToken)
        }
        return result
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