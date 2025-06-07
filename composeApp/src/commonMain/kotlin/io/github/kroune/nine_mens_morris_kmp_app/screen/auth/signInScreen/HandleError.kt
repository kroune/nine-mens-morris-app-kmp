package io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signInScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginApiResponse
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import ninemensmorrisappkmp.composeapp.generated.resources.wrong_pass_or_login
import org.jetbrains.compose.resources.getString


@Composable
fun HandleSignInError(
    registrationResult: LoginApiResponse?,
    accountIdByJwtTokenResult: AccountIdByJwtTokenApiResponses?,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(registrationResult, accountIdByJwtTokenResult) {
        val text = when (registrationResult) {
            null -> {
                return@LaunchedEffect
            }

            is LoginApiResponse.Success -> {
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

            is LoginApiResponse.UnknownError -> {
                getString(Res.string.unknown_error)
            }

            is LoginApiResponse.NetworkError -> {
                getString(Res.string.network_error)
            }

            is LoginApiResponse.ServerError -> {
                getString(Res.string.server_error)
            }

            is LoginApiResponse.CredentialsError -> {
                getString(Res.string.wrong_pass_or_login)
            }
        }
        snackbarHostState.showSnackbar(text)
    }
}
