package io.github.kroune.nine_mens_morris_kmp_app.repositories.auth

import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses

/**
 * interface for auth repository
 * @see AuthRepositoryImpl
 */
interface AuthRepositoryI {
    suspend fun login(login: String, password: String): LoginApiResponse

    suspend fun register(login: String, password: String): RegisterApiResponses

    /**
     * Validates the provided login.
     *
     * @param login The login to be validated.
     * @return True if the login is valid, false otherwise.
     */
    fun loginValidator(login: String): Boolean

    /**
     * Validates the provided password.
     *
     * @param password The password to be validated.
     * @return True if the password is valid, false otherwise.
     */
    fun passwordValidator(password: String): Boolean
}