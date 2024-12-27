package com.kroune.nine_mens_morris_kmp_app.component.auth.signIn

import com.kroune.nine_mens_morris_kmp_app.event.auth.SignInScreenEvent

interface SignInScreenComponentI {
    val username: String
    val usernameValid: Boolean
    val password: String
    val passwordValid: Boolean
    val loginResult: Result<*>?
    val loginInProcess: Boolean
    fun onEvent(event: SignInScreenEvent)
}
