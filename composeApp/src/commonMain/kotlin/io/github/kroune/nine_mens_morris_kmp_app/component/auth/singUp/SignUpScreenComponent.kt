package io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.event.auth.SignUpScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.interactors.accountIdInteractor
import io.github.kroune.nine_mens_morris_kmp_app.interactors.authRepositoryInteractor
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RegisterApiResponses
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpScreenComponent(
    val onNavigationBack: () -> Unit,
    val onNavigationToSignInScreen: () -> Unit,
    val onSuccessfulAuth: () -> Unit,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = CoroutineScope(Dispatchers.Default)

    private val _state = MutableStateFlow(
        SignUpScreenState(
            username = "",
            isUsernameValid = false,
            password = "",
            isPasswordValid = false,
            passwordRepeated = "",
            isPasswordRepeatedValid = false,
            registrationResult = null,
            accountIdByJwtTokenResult = null,
            registrationInProcess = false
        )
    )
    val state: StateFlow<SignUpScreenState> = _state

    private fun updateUsername(newUsername: String) {
        _state.value = _state.value.copy(
            username = newUsername,
            isUsernameValid = authRepositoryInteractor.loginValidator(newUsername)
        )
    }

    private fun updatePassword(newPassword: String) {
        _state.value = _state.value.copy(
            password = newPassword,
            isPasswordValid = authRepositoryInteractor.passwordValidator(newPassword),
            isPasswordRepeatedValid = (state.value.passwordRepeated == newPassword)
        )
    }

    private fun updatePasswordRepeated(newPasswordRepeated: String) {
        _state.value = _state.value.copy(
            passwordRepeated = newPasswordRepeated,
            isPasswordRepeatedValid = (state.value.password == newPasswordRepeated)
        )
    }

    private fun register() {
        _state.update {
            it.copy(
                registrationInProcess = true
            )
        }
        componentScope.launch {
            val registerResult = authRepositoryInteractor.register(
                state.value.username,
                state.value.password
            )
            var accountId: AccountIdByJwtTokenApiResponses? = null
            if (registerResult is RegisterApiResponses.Success) {
                accountId = accountIdInteractor.getAccountId()
                if (accountId is AccountIdByJwtTokenApiResponses.Success) {
                    withContext(Dispatchers.Main) {
                        onSuccessfulAuth()
                    }
                }
            }
            _state.update {
                it.copy(
                    registrationResult = registerResult,
                    registrationInProcess = false,
                    accountIdByJwtTokenResult = accountId
                )
            }
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

            is SignUpScreenEvent.UpdatePassword -> {
                updatePassword(event.newPassword)
            }
            is SignUpScreenEvent.UpdateRepeatedPassword -> {
                updatePasswordRepeated(event.newRepeatedPassword)
            }
            is SignUpScreenEvent.UpdateUsername -> {
                updateUsername(event.newUsername)
            }
        }
    }

    override fun onBackPressed() {
        onEvent(SignUpScreenEvent.Back)
    }
}

@Immutable
data class SignUpScreenState(
    val username: String,
    val isUsernameValid: Boolean,
    val password: String,
    val isPasswordValid: Boolean,
    val passwordRepeated: String,
    val isPasswordRepeatedValid: Boolean,
    val registrationResult: RegisterApiResponses?,
    val accountIdByJwtTokenResult: AccountIdByJwtTokenApiResponses?,
    val registrationInProcess: Boolean
)
