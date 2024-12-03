package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.data.remote.UploadPictureApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import com.kroune.nine_mens_morris_kmp_app.interactors.accountInfoInteractor
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.client_error
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
fun ViewAccountScreen(
    component: ViewAccountScreenComponent
) {
    val isOwnAccount = component.isOwnAccount
    val name = component.accountName
    val rating = component.accountRating
    val creationDate = component.accountCreationDate
    val picture = component.accountPicture
    val onEvent: (ViewAccountScreenEvent) -> Unit = { component.onEvent(it) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backgroundColor = Color.Transparent
    ) { _ ->
        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.175f)
                    .fillMaxWidth()
            ) {
                DrawIcon(
                    pictureByteArray = picture,
                    onReload = { onEvent(ViewAccountScreenEvent.ReloadIcon) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
                DrawName(
                    {
                        Text(it)
                    },
                    name,
                    { onEvent(ViewAccountScreenEvent.ReloadName) },
                    scope,
                    snackbarHostState
                )
            }
            DrawRating(
                text = {
                    Text("${stringResource(Res.string.rating)}: $it")
                },
                accountRating = rating,
                reloadRating = { onEvent(ViewAccountScreenEvent.ReloadRating) },
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            DrawAccountCreationDate(
                { (first, second, third) -> "$first-$second-$third" },
                creationDate, onEvent, scope, snackbarHostState
            )
            val launcher = rememberFilePickerLauncher(
                type = PickerType.Image,
                mode = PickerMode.Single
            ) { file ->
                if (file == null) {
                    return@rememberFilePickerLauncher
                }
                CoroutineScope(Dispatchers.Default).launch {
                    // Handle picked files
                    val result = accountInfoInteractor.uploadPicture(file.readBytes())
                    result.onSuccess {
                        snackbarHostState.showSnackbar(getString(Res.string.image_was_updated))
                    }.onFailure {
                        val text = when (it) {
                            !is UploadPictureApiResponses -> {
                                getString(Res.string.unknown_error)
                            }
                            is UploadPictureApiResponses.ClientError -> {
                                getString(Res.string.client_error)
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
                                getString(Res.string.image_too_large, it.maxWidth, it.maxHeight)
                            }

                            else -> {
                                error("kotlin broke")
                            }
                        }
                        snackbarHostState.showSnackbar(text)
                    }
                }
            }
            Button({
                launcher.launch()
            }) {
                Text(stringResource(Res.string.upload_picture))
            }
            if (isOwnAccount) {
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
        }
    }
}

/**
 * draws specific settings for our account
 */
@Composable
fun DrawOwnAccountOptions(
    onEvent: (ViewAccountScreenEvent) -> Unit
) {
    Button(
        onClick = {
            onEvent(ViewAccountScreenEvent.Logout)
        }
    ) {
        Text(stringResource(Res.string.log_out))
    }
}
