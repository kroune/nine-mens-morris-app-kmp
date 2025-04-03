package io.github.kroune.nine_mens_morris_kmp_app.screen.other

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenComponent
import io.github.kroune.nine_mens_morris_kmp_app.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.model.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawAccountCreationDate
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.DrawRating
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.image_too_large
import ninemensmorrisappkmp.composeapp.generated.resources.image_was_updated
import ninemensmorrisappkmp.composeapp.generated.resources.log_out
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import ninemensmorrisappkmp.composeapp.generated.resources.upload_picture
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewOwnAccountScreen(
    component: ViewOwnAccountScreenComponent
) {
    val onEvent: (ViewOwnAccountScreenEvent) -> Unit = { component.onEvent(it) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    with(component) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { _ ->
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    DrawIcon(
                        Modifier
                            .fillMaxWidth(0.45f)
                            .aspectRatio(1f),
                        pictureByteArray = accountPicture,
                        onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadIcon) },
                        onClick = {},
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                    DrawName(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        text = {
                            Text(
                                it,
                                fontSize = 30.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        accountName = accountName,
                        onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadName) },
                        scope = scope,
                        snackbarHostState = snackbarHostState
                    )
                }
                DrawRating(
                    text = {
                        Text(
                            "${stringResource(Res.string.rating)}: $it",
                            fontSize = 20.sp
                        )
                    },
                    accountRating = accountRating,
                    reloadRating = { onEvent(ViewOwnAccountScreenEvent.ReloadRating) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
                DrawAccountCreationDate(
                    { (first, second, third) ->
                        Text(
                            "$first-$second-$third",
                            fontSize = 20.sp
                        )
                    },
                    accountCreationDate,
                    {
                        onEvent(ViewOwnAccountScreenEvent.ReloadCreationDate)
                    },
                    scope, snackbarHostState
                )
                val launcher = rememberFilePickerLauncher(
                    type = PickerType.Image,
                    mode = PickerMode.Single
                ) { file ->
                    if (file == null) {
                        return@rememberFilePickerLauncher
                    }
                    CoroutineScope(Dispatchers.Default).launch {
                        component.onEvent(ViewOwnAccountScreenEvent.UploadNewPicture(file.readBytes()))
                    }
                }
                Button(
                    { launcher.launch() },
                ) {
                    Text(stringResource(Res.string.upload_picture))
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    DrawOwnAccountOptions(
                        onEvent
                    )
                }
            }
            HandleError(uploadingNewPicture, snackbarHostState, scope)
        }
    }
}

@Composable
private fun HandleError(
    uploadingNewPicture: UploadPictureApiResponses?,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val text = when (uploadingNewPicture) {
        is UploadPictureApiResponses.Success -> {
            stringResource(Res.string.image_was_updated)
        }

        is UploadPictureApiResponses.ServerError -> {
            stringResource(Res.string.server_error)
        }

        is UploadPictureApiResponses.NetworkError -> {
            stringResource(Res.string.network_error)
        }

        is UploadPictureApiResponses.CredentialsError -> {
            stringResource(Res.string.credentials_error)
        }

        is UploadPictureApiResponses.TooLargeImage -> {
            stringResource(
                Res.string.image_too_large,
                uploadingNewPicture.maxWidth,
                uploadingNewPicture.maxHeight
            )
        }

        UploadPictureApiResponses.UnknownError -> {
            stringResource(Res.string.unknown_error)
        }

        null -> return
    }
    scope.launch {
        snackbarHostState.showSnackbar(text)
    }
}

/**
 * draws specific settings for our account
 */
@Composable
fun DrawOwnAccountOptions(
    onEvent: (ViewOwnAccountScreenEvent) -> Unit
) {
    Button(
        onClick = {
            onEvent(ViewOwnAccountScreenEvent.Logout)
        },
    ) {
        Text(stringResource(Res.string.log_out))
    }
}
