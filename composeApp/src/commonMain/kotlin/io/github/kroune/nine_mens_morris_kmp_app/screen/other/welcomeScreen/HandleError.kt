package io.github.kroune.nine_mens_morris_kmp_app.screen.other.welcomeScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountIdByJwtTokenApiResponses
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString

@Composable
fun HandleWelcomeScreenError(
    result: AccountIdByJwtTokenApiResponses?,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(result) {
        val text: String = when (result) {
            is AccountIdByJwtTokenApiResponses.UnknownError -> {
                getString(Res.string.unknown_error)
            }

            is AccountIdByJwtTokenApiResponses.NetworkError -> {
                getString(Res.string.network_error)
            }

            is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                getString(Res.string.credentials_error)
            }

            is AccountIdByJwtTokenApiResponses.ServerError -> {
                getString(Res.string.server_error)
            }

            is AccountIdByJwtTokenApiResponses.Success, null -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(text)
    }
}
