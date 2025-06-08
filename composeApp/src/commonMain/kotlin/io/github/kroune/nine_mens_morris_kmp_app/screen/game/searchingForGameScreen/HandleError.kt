package io.github.kroune.nine_mens_morris_kmp_app.screen.game.searchingForGameScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.image_was_updated
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString

@Composable
fun HandleSearchingForGameError(
    uploadingNewPicture: SearchingForGameResponse?,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(uploadingNewPicture) {
        val text = when (uploadingNewPicture) {
            is SearchingForGameResponse.Success -> {
                getString(Res.string.image_was_updated)
            }

            is SearchingForGameResponse.ServerError -> {
                getString(Res.string.server_error)
            }

            is SearchingForGameResponse.NetworkError -> {
                getString(Res.string.network_error)
            }

            is SearchingForGameResponse.UnknownError -> {
                getString(Res.string.unknown_error)
            }

            null -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(text)
    }
}
