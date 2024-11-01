package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.authRepositoryInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.AccountIdByJwtTokenApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RegisterApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.SignUpScreenEvent
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpScreenComponent(
    val onNavigationToSignInScreen: ((Long) -> Configuration) -> Unit,
    val switchingScreensLambda: (Configuration) -> Unit,
    val nextScreen: (Long) -> Configuration,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    var username by mutableStateOf<String>("")
    var usernameValid by mutableStateOf<Boolean>(false)
    var password by mutableStateOf<String>("")
    var passwordValid by mutableStateOf<Boolean>(false)
    var passwordRepeated by mutableStateOf<String>("")
    var passwordRepeatedMatches by mutableStateOf<Boolean>(false)

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
                            registrationResult = Result.failure<Any>(RegisterApiResponses.ClientError)
                        }
                        is AccountIdByJwtTokenApiResponses.NetworkError -> {
                            registrationResult = Result.failure<Any>(RegisterApiResponses.NetworkError)
                        }
                        is AccountIdByJwtTokenApiResponses.ServerError -> {
                            registrationResult = Result.failure<Any>(RegisterApiResponses.ServerError)
                        }
                        is AccountIdByJwtTokenApiResponses.ClientError -> {
                            registrationResult = Result.failure<Any>(RegisterApiResponses.ClientError)
                        }
                        // first stage was successful (credentials are valid), but the second failed
                        is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                            registrationResult = Result.failure<Any>(RegisterApiResponses.ClientError)
                        }
                    }
                }
                accountId.onSuccess {
                    withContext(Dispatchers.Main) {
                        switchingScreensLambda(nextScreen(it))
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
                onNavigationToSignInScreen(nextScreen)
            }
        }
    }
}