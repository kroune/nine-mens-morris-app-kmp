package io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp

import androidx.compose.runtime.Immutable
import com.arkivanov.decompose.ComponentContext
import io.github.kroune.nine_mens_morris_kmp_app.component.ComponentContextWithBackHandle
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.auth.SignUpScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.accountId.AccountIdRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.domain.repositories.auth.AuthRepositoryI
import io.github.kroune.nine_mens_morris_kmp_app.component.componentCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpScreenComponent(
    private val onNavigationBack: () -> Unit,
    private val onNavigationToSignInScreen: () -> Unit,
    private val onSuccessfulAuth: () -> Unit,
    private val accountIdRepository: AccountIdRepositoryI,
    private val authRepository: AuthRepositoryI,
    componentContext: ComponentContext
) : ComponentContext by componentContext, ComponentContextWithBackHandle {
    private val componentScope = componentCoroutineScope()

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
            isUsernameValid = authRepository.loginValidator(newUsername)
        )
    }

    private fun updatePassword(newPassword: String) {
        _state.value = _state.value.copy(
            password = newPassword,
            isPasswordValid = authRepository.passwordValidator(newPassword),
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
            val registerResult = authRepository.register(
                state.value.username,
                state.value.password
            )
            var accountId: AccountIdByJwtTokenApiResponses? = null
            if (registerResult is RegisterApiResponses.Success) {
                accountId = accountIdRepository.getAccountId()
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
