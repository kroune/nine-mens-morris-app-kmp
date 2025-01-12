package com.kroune.nine_mens_morris_kmp_app.component.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountIdByJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.RegisterApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.auth.SignUpScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.interactors.authRepositoryInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpScreenComponent(
    val onNavigationBack: () -> Unit,
    val onNavigationToSignInScreen: () -> Unit,
    val onSuccessfulAuth: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {

    var username by mutableStateOf("")
    var usernameValid by mutableStateOf(false)
    var password by mutableStateOf("")
    var passwordValid by mutableStateOf(false)
    var passwordRepeated by mutableStateOf("")
    var passwordRepeatedMatches by mutableStateOf(false)

    var registrationResult: Result<*>? by mutableStateOf(null)
    var registrationInProcess by mutableStateOf(false)

    fun updateUsername(newUsername: String) {
        username = newUsername
        usernameValid = authRepositoryInteractor.loginValidator(newUsername)
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        passwordValid = authRepositoryInteractor.passwordValidator(newPassword)
        passwordRepeatedMatches = (password == passwordRepeated)
    }

    fun updatePasswordRepeated(newPasswordRepeated: String) {
        passwordRepeated = newPasswordRepeated
        passwordRepeatedMatches = (password == passwordRepeated)
    }

    private fun register() {
        CoroutineScope(Dispatchers.Default).launch {
            registrationInProcess = true
            val jwtToken = authRepositoryInteractor.register(username, password)
            jwtToken.onSuccess {
                val accountId = accountIdInteractor.getAccountId()
                accountId.onFailure {
                    when (it) {
                        !is AccountIdByJwtTokenApiResponses -> {
                            registrationResult =
                                Result.failure<Any>(RegisterApiResponses.ClientError)
                        }

                        is AccountIdByJwtTokenApiResponses.NetworkError -> {
                            registrationResult =
                                Result.failure<Any>(RegisterApiResponses.NetworkError)
                        }

                        is AccountIdByJwtTokenApiResponses.ServerError -> {
                            registrationResult =
                                Result.failure<Any>(RegisterApiResponses.ServerError)
                        }

                        is AccountIdByJwtTokenApiResponses.ClientError -> {
                            registrationResult =
                                Result.failure<Any>(RegisterApiResponses.ClientError)
                        }
                        // first stage was successful (credentials are valid), but the second failed
                        is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                            registrationResult =
                                Result.failure<Any>(RegisterApiResponses.ClientError)
                        }
                    }
                }
                accountId.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccessfulAuth()
                    }
                }
            }
            registrationResult = jwtToken
            registrationInProcess = false
        }
    }

    fun onEvent(event: SignUpScreenEvent) {
        when (event) {
            SignUpScreenEvent.Register -> {
                register()
            }

            SignUpScreenEvent.SwitchToSignInScreen -> {
                onNavigationToSignInScreen()
            }

            SignUpScreenEvent.Back -> {
                onNavigationBack()
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SignUpScreenEvent.Back)
    }
}