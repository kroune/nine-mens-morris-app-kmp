package com.kroune.nine_mens_morris_kmp_app.component

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.accountIdInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.interactors.authRepositoryInteractor
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.LoginByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.SignInScreenEvent
import com.kroune.nine_mens_morris_kmp_app.navigation.RootComponent.Configuration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInScreenComponent(
    val onNavigationToSignUpScreen: ((Long) -> Configuration) -> Unit,
    val switchingScreensLambda: (Configuration) -> Unit,
    val nextScreen: (Long) -> Configuration,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    var username by mutableStateOf<String>("")
    var usernameValid by mutableStateOf<Boolean>(false)
    var password by mutableStateOf<String>("")
    var passwordValid by mutableStateOf<Boolean>(false)

    var loginResult: Result<*>? by mutableStateOf(null)
    var loginInProcess by mutableStateOf(false)

    fun updateUsername(newUsername: String) {
        username = newUsername
        usernameValid = authRepositoryInteractor.loginValidator(newUsername)
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
        passwordValid = authRepositoryInteractor.passwordValidator(newPassword)
    }

    private fun login() {
        CoroutineScope(Dispatchers.Default).launch {
            loginInProcess = true
            val jwtToken = authRepositoryInteractor.login(username, password)
            jwtToken.onSuccess {
                val accountId = accountIdInteractor.getAccountId()
                accountId.onFailure {
                    // TODO: refactor this
                    loginResult = Result.failure<Any>(LoginByIdApiResponses.ClientError)
                }
                accountId.onSuccess {
                    withContext(Dispatchers.Main) {
                        switchingScreensLambda(nextScreen(it))
                    }
                }
            }
            loginResult = (jwtToken as Result<*>?)
            loginInProcess = false
        }
    }

    fun onEvent(event: SignInScreenEvent) {
        when (event) {
            SignInScreenEvent.Login -> {
                login()
            }

            SignInScreenEvent.SwitchToSignInScreen -> {
                onNavigationToSignUpScreen(nextScreen)
            }
        }
    }
}