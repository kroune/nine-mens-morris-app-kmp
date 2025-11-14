package io.github.kroune.nine_mens_morris_kmp_app.screen.other.viewOwnAccountScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.UploadPictureApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.padding2
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawAccountCreationDate
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawRating
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
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
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewOwnAccountScreen(
    onEvent: (ViewOwnAccountScreenEvent) -> Unit,
    state: ViewOwnAccountScreenState,
    uploadingNewPictureResult: SharedFlow<UploadPictureApiResponses>,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .safeDrawingPadding()
                    .padding(bottom = padding2),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Button(
                    onClick = { onEvent(ViewOwnAccountScreenEvent.OnLogoutPressed) },
                    shape = RoundedCornerShape3,
                ) {
                    Text(stringResource(Res.string.log_out))
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .padding(horizontal = padding2)
                .padding(top = padding2),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                BoxWithConstraints {
                    val size = min(this.maxWidth, this.maxHeight) / 2
                    DrawIcon(
                        Modifier
                            .size(size)
                            .aspectRatio(1f, true),
                        pictureByteArray = state.accountPictureResult,
                        onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadIcon) },
                        onClick = {},
                        snackbarHostState = snackbarHostState
                    )
                }
                DrawName(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    onSuccess = {
                        Text(
                            it,
                            fontSize = 30.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    accountName = state.accountLoginResult,
                    onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadName) },
                    snackbarHostState = snackbarHostState
                )
            }
            DrawRating(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(30.dp),
                onSuccess = {
                    Text(
                        "${stringResource(Res.string.rating)}: $it",
                        fontSize = 20.sp
                    )
                },
                accountRating = state.accountRatingResult,
                onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadRating) },
                snackbarHostState = snackbarHostState
            )
            DrawAccountCreationDate(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(30.dp),
                onSuccess = { (first, second, third) ->
                    Text(
                        "$first.$second.$third",
                        fontSize = 20.sp
                    )
                },
                accountCreationDate = state.accountCreationDateResult,
                onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadCreationDate) },
                snackbarHostState = snackbarHostState,
            )
            val launcher = rememberFilePickerLauncher(
                type = PickerType.Image,
                mode = PickerMode.Single
            ) { file ->
                if (file == null) {
                    return@rememberFilePickerLauncher
                }
                scope.launch {
                    onEvent(ViewOwnAccountScreenEvent.UploadNewPicture(file.readBytes()))
                }
            }
            Button(
                { launcher.launch() },
                shape = RoundedCornerShape3,
            ) {
                Text(stringResource(Res.string.upload_picture))
            }
        }
    }
    HandleError(
        uploadingNewPictureResult,
        snackbarHostState,
    )
}

@Composable
fun HandleError(
    uploadingNewPictureResult: SharedFlow<UploadPictureApiResponses>,
    snackbarHostState: SnackbarHostState,
) {
    LaunchedEffect(uploadingNewPictureResult) {
        uploadingNewPictureResult.collectLatest {
            val text = when (it) {
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
                        it.maxWidth,
                        it.maxHeight,
                    )
                }

                is UploadPictureApiResponses.UnknownError -> {
                    getString(Res.string.unknown_error)
                }
            }
            snackbarHostState.showSnackbar(text)
        }
    }
}
