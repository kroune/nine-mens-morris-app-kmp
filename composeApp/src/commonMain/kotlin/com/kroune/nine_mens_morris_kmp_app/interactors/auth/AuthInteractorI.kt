package com.kroune.nine_mens_morris_kmp_app.interactors.auth

/**
 * interface for auth repository
 * @see AuthInteractorImpl
 */
interface AuthInteractorI {
    suspend fun login(login: String, password: String): Result<String>

    suspend fun register(login: String, password: String): Result<String>

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