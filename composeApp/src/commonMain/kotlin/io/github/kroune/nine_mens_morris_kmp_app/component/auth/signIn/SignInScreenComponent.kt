package io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.data.remote.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.event.auth.SignInScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.authRepositoryInteractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInScreenComponent(
    val onNavigationBack: () -> Unit,
    val onNavigationToSignUpScreen: () -> Unit,
    val onSuccessfulAuth: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle, SignInScreenComponentI {
    override var username by mutableStateOf("")
    override var usernameValid by mutableStateOf(false)
    override var password by mutableStateOf("")
    override var passwordValid by mutableStateOf(false)

    override var loginResult: Result<*>? by mutableStateOf(null)
    override var loginInProcess by mutableStateOf(false)

    private fun login() {
        CoroutineScope(Dispatchers.Default).launch {
            loginInProcess = true
            val jwtToken = authRepositoryInteractor.login(username, password)
            jwtToken.onSuccess {
                val accountId = accountIdInteractor.getAccountId()
                accountId.onFailure {
                    when (it) {
                        !is AccountIdByJwtTokenApiResponses -> {
                            loginResult = Result.failure<Any>(LoginByIdApiResponses.ClientError)
                        }

                        is AccountIdByJwtTokenApiResponses.NetworkError -> {
                            loginResult = Result.failure<Any>(LoginByIdApiResponses.NetworkError)
                        }

                        is AccountIdByJwtTokenApiResponses.ServerError -> {
                            loginResult = Result.failure<Any>(LoginByIdApiResponses.ServerError)
                        }

                        is AccountIdByJwtTokenApiResponses.ClientError -> {
                            loginResult = Result.failure<Any>(LoginByIdApiResponses.ClientError)
                        }
                        // first stage was successful (credentials are valid), but the second failed
                        is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                            loginResult = Result.failure<Any>(LoginByIdApiResponses.ClientError)
                        }
                    }
                }
                accountId.onSuccess {
                    withContext(Dispatchers.Main) {
                        onSuccessfulAuth()
                    }
                }
            }
            loginResult = jwtToken
            loginInProcess = false
        }
    }

    override fun onEvent(event: SignInScreenEvent) {
        when (event) {
            SignInScreenEvent.Login -> {
                login()
            }

            SignInScreenEvent.SwitchToSignInScreen -> {
                onNavigationToSignUpScreen()
            }

            SignInScreenEvent.Back -> {
                onNavigationBack()
            }

            is SignInScreenEvent.UsernameUpdate -> {
                username = event.newText
                usernameValid = authRepositoryInteractor.loginValidator(event.newText)
            }

            is SignInScreenEvent.PasswordUpdate -> {
                password = event.newText
                passwordValid = authRepositoryInteractor.passwordValidator(event.newText)
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SignInScreenEvent.Back)
    }
}