package io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.model.event.auth.SignInScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.authRepositoryInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.screen.componentCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInScreenComponent(
    val onNavigationBack: () -> Unit,
    val onNavigationToSignUpScreen: () -> Unit,
    val onSuccessfulAuth: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

    private val _state: MutableStateFlow<SignInScreenState> = MutableStateFlow(
        SignInScreenState(
            username = "",
            isUsernameValid = false,
            password = "",
            isPasswordValid = false,
            loginResult = null,
            accountIdByJwtTokenResult = null,
            requestInProcess = false
        )
    )
    val state: StateFlow<SignInScreenState>
        get() = _state

    private fun login() {
        _state.update {
            it.copy(
                requestInProcess = true
            )
        }
        componentScope.launch {
            val jwtTokenResult = authRepositoryInteractor.login(
                state.value.username,
                state.value.password
            )
            var accountIdResult: AccountIdByJwtTokenApiResponses? = null
            if (jwtTokenResult is LoginApiResponse.Success) {
                accountIdResult = accountIdInteractor.getAccountId()
                if (accountIdResult is AccountIdByJwtTokenApiResponses.Success) {
                    withContext(Dispatchers.Main) {
                        onSuccessfulAuth()
                    }
                }
            }
            _state.update {
                it.copy(
                    loginResult = jwtTokenResult,
                    accountIdByJwtTokenResult = accountIdResult,
                    requestInProcess = false
                )
            }
        }
    }

    fun onEvent(event: SignInScreenEvent) {
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
                _state.update {
                    it.copy(
                        username = event.newText,
                        isUsernameValid = authRepositoryInteractor.loginValidator(event.newText)
                    )
                }
            }

            is SignInScreenEvent.PasswordUpdate -> {
                _state.update {
                    it.copy(
                        password = event.newText,
                        isPasswordValid = authRepositoryInteractor.passwordValidator(event.newText)
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SignInScreenEvent.Back)
    }
}

@Immutable
data class SignInScreenState(
    val username: String,
    val isUsernameValid: Boolean,
    val password: String,
    val isPasswordValid: Boolean,
    val loginResult: LoginApiResponse?,
    val accountIdByJwtTokenResult: AccountIdByJwtTokenApiResponses?,
    val requestInProcess: Boolean
)