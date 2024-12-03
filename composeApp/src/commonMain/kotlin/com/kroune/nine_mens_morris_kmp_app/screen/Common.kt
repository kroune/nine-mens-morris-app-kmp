package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.data.remote.AccountPictureByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.CreationDateByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.LoginByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.data.remote.RatingByIdApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.client_error
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.retry
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


/**
 * draws user rating
 */
@Composable
fun DrawRating(
    text: @Composable (Long) -> @Composable Unit,
    accountRating: Result<Long>?,
    reloadRating: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    AnimatedContent(
        accountRating,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(3000)
            ) togetherWith fadeOut(animationSpec = tween(3000))
        },
        contentAlignment = Alignment.Center
    ) {
        when {
            it == null -> {
                Box(
                    modifier = Modifier
                        .aspectRatio(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    LoadingCircle()
                }
            }

            it.isSuccess -> {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    text(it.getOrThrow())
                }
            }

            it.isFailure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = {
                        reloadRating()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            modifier = Modifier
                                .fillMaxSize()
                                .aspectRatio(1f)
                                .clip(CircleShape)
                        )
                    }
                }
                val exception = it.exceptionOrNull()
                val exceptionText: String = when (exception) {
                    !is RatingByIdApiResponses -> {
                        stringResource(Res.string.unknown_error)
                    }

                    is RatingByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    RatingByIdApiResponses.ClientError -> {
                        stringResource(Res.string.client_error)
                    }

                    RatingByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    RatingByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    else -> {
                        error("kotlin broke")
                    }
                }
                val retryText = stringResource(Res.string.retry)
                scope.launch {
                    snackbarHostState.showSnackbar(exceptionText, retryText).let { snackbarResult ->
                        if (snackbarResult == SnackbarResult.ActionPerformed) {
                            reloadRating()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawAccountCreationDate(
    text: @Composable (Triple<Int, Int, Int>) -> String,
    accountCreationDate: Result<Triple<Int, Int, Int>>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountCreationDate == null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountCreationDate.isSuccess -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text(accountCreationDate.getOrThrow()),
                    fontSize = 20.sp
                )
            }
        }

        accountCreationDate.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountCreationDate.exceptionOrNull()!!
            val exceptionText: String = when (exception) {
                !is CreationDateByIdApiResponses -> {
                    stringResource(Res.string.unknown_error)
                }

                is CreationDateByIdApiResponses.NetworkError -> {
                    stringResource(Res.string.network_error)
                }

                CreationDateByIdApiResponses.ClientError -> {
                    stringResource(Res.string.client_error)
                }

                CreationDateByIdApiResponses.CredentialsError -> {
                    stringResource(Res.string.credentials_error)
                }

                CreationDateByIdApiResponses.ServerError -> {
                    stringResource(Res.string.server_error)
                }

                else -> {
                    error("kotlin broke")
                }
            }
            val retryText = stringResource(Res.string.retry)
            scope.launch {
                snackbarHostState.showSnackbar(exceptionText, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawIcon(
    modifier: Modifier = Modifier,
    pictureByteArray: Result<ByteArray>?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    AnimatedContent(
        pictureByteArray,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(450))
        }
    ) {
        when {
            it == null -> {
                Box(
                    modifier = modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingCircle()
                }
            }

            it.isSuccess -> {
                Image(
                    bitmap = it.getOrThrow().decodeToImageBitmap(),
                    contentDescription = "Profile icon",
                    modifier = modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                )
            }

            it.isFailure -> {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    IconButton(onClick = {
                        onReload()
                    }) {
                        Icon(
                            painter = painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            modifier = modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .clip(CircleShape)
                        )
                    }
                }
                val exception = it.exceptionOrNull()!!
                val text: String = when (exception) {
                    !is AccountPictureByIdApiResponses -> {
                        stringResource(Res.string.unknown_error)
                    }

                    is AccountPictureByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    AccountPictureByIdApiResponses.ClientError -> {
                        stringResource(Res.string.client_error)
                    }

                    AccountPictureByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    AccountPictureByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    else -> {
                        error("kotlin broke")
                    }
                }
                val retryText = stringResource(Res.string.retry)
                scope.launch {
                    snackbarHostState.showSnackbar(text, retryText).let { result ->
                        if (result == SnackbarResult.ActionPerformed) {
                            onReload()
                        }
                    }
                }
            }
        }
    }
}


/**
 * draws user name or loading animation
 */
@Composable
fun DrawName(
    text: @Composable (String) -> Unit = @Composable {
        Text(it, fontSize = 20.sp)
    },
    accountName: Result<String>?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountName == null -> {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountName.isSuccess -> {
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                text(accountName.getOrThrow())
            }
        }

        accountName.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onReload()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountName.exceptionOrNull()
            val exceptionText: String = when (exception) {
                !is LoginByIdApiResponses -> {
                    stringResource(Res.string.unknown_error)
                }

                is LoginByIdApiResponses.NetworkError -> {
                    stringResource(Res.string.network_error)
                }

                LoginByIdApiResponses.ClientError -> {
                    stringResource(Res.string.client_error)
                }

                LoginByIdApiResponses.CredentialsError -> {
                    stringResource(Res.string.credentials_error)
                }

                LoginByIdApiResponses.ServerError -> {
                    stringResource(Res.string.server_error)
                }

                else -> {
                    error("kotlin broke")
                }
            }
            val retryText = stringResource(Res.string.retry)
            scope.launch {
                snackbarHostState.showSnackbar(exceptionText, retryText).let { result ->
                    if (result == SnackbarResult.ActionPerformed) {
                        onReload()
                    }
                }
            }
        }
    }
}

@Composable
fun BackHandler(backHandler: BackHandler, isEnabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val callback =
        remember {
            BackCallback(isEnabled = isEnabled) {
                currentOnBack()
            }
        }
    SideEffect { callback.isEnabled = isEnabled }
    DisposableEffect(backHandler) {
        backHandler.register(callback)
        onDispose { backHandler.unregister(callback) }
    }
}
