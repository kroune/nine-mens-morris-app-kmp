package io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signUpScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RegisterApiResponses
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.login_in_use
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString

@Composable
fun HandleSignUpError(
    registrationResult: RegisterApiResponses?,
    accountIdByJwtTokenResult: AccountIdByJwtTokenApiResponses?,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(registrationResult) {
        val text = when (registrationResult) {
            null -> {
                return@LaunchedEffect
            }

            is RegisterApiResponses.Success -> {
                when (accountIdByJwtTokenResult) {
                    is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                        getString(Res.string.credentials_error)
                    }

                    is AccountIdByJwtTokenApiResponses.NetworkError -> {
                        getString(Res.string.network_error)
                    }

                    is AccountIdByJwtTokenApiResponses.ServerError -> {
                        getString(Res.string.server_error)
                    }

                    is AccountIdByJwtTokenApiResponses.UnknownError -> {
                        getString(Res.string.unknown_error)
                    }

                    is AccountIdByJwtTokenApiResponses.Success, null -> return@LaunchedEffect
                }
            }

            is RegisterApiResponses.UnknownError -> {
                getString(Res.string.unknown_error)
            }

            is RegisterApiResponses.LoginAlreadyInUse -> {
                getString(Res.string.login_in_use)
            }

            is RegisterApiResponses.NetworkError -> {
                getString(Res.string.network_error)
            }

            is RegisterApiResponses.ServerError -> {
                getString(Res.string.server_error)
            }
        }
        snackbarHostState.showSnackbar(text)
    }
}
