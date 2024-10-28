package com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.auth

import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.jwtTokenRepositoryInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.authRepository

class AuthInteractorImpl : AuthInteractorI {
    val authRemote = authRepository

    override suspend fun login(
        login: String,
        password: String
    ): Result<String> {
        val result = authRemote.login(login, password).onSuccess { jwtToken ->
            jwtTokenRepositoryInteractor.updateJwtToken(jwtToken)
        }
        return result
    }

    override suspend fun register(
        login: String,
        password: String
    ): Result<String> {
        val result = authRemote.register(login, password).onSuccess { jwtToken ->
            jwtTokenRepositoryInteractor.updateJwtToken(jwtToken)
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