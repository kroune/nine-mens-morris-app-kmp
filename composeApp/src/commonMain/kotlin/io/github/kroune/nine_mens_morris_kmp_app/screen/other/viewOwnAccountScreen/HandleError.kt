package io.github.kroune.nine_mens_morris_kmp_app.screen.other.viewOwnAccountScreen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.image_too_large
import ninemensmorrisappkmp.composeapp.generated.resources.image_was_updated
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString

@Composable
fun HandleOwnAccountScreenError(
    uploadingNewPicture: UploadPictureApiResponses?,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(uploadingNewPicture) {
        val text = when (uploadingNewPicture) {
            is UploadPictureApiResponses.Success -> {
                getString(Res.string.image_was_updated)
            }

            is UploadPictureApiResponses.ServerError -> {
                getString(Res.string.server_error)
            }

            is UploadPictureApiResponses.NetworkError -> {
                getString(Res.string.network_error)
            }

            is UploadPictureApiResponses.CredentialsError -> {
                getString(Res.string.credentials_error)
            }

            is UploadPictureApiResponses.TooLargeImage -> {
                getString(
                    Res.string.image_too_large,
                    uploadingNewPicture.maxWidth,
                    uploadingNewPicture.maxHeight
                )
            }

            is UploadPictureApiResponses.UnknownError -> {
                getString(Res.string.unknown_error)
            }

            null -> return@LaunchedEffect
        }
        snackbarHostState.showSnackbar(text)
    }
}
