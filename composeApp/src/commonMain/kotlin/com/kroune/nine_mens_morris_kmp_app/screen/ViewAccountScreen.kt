package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
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
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.175f)
                    .fillMaxWidth()
            ) {
                DrawIcon(picture, onEvent, scope, snackbarHostState)
                DrawName(name, onEvent, scope, snackbarHostState)
            }
            DrawRating(rating, onEvent, scope, snackbarHostState)
            DrawAccountCreationDate(creationDate, onEvent, scope, snackbarHostState)
            if (isOwnAccount) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
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
 * draws user rating
 */
@Composable
fun DrawRating(
    accountRating: Result<Long>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountRating == null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountRating.isSuccess -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "User rating is ${accountRating.getOrThrow()}",
                    fontSize = 20.sp
                )
            }
        }

        accountRating.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadRating)
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .padding(start = 12.dp, top = 12.dp)
                            .fillMaxHeight(0.1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountRating.exceptionOrNull()
            val text: String = when (exception) {
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
                snackbarHostState.showSnackbar(text, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onEvent(ViewAccountScreenEvent.ReloadRating)
                    }
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
        Text("Log out")
    }
}

@Composable
fun DrawAccountCreationDate(
    accountCreationDate: Result<Triple<Int, Int, Int>>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountCreationDate == null -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountCreationDate.isSuccess -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    accountCreationDate.getOrThrow().run { "$first-$second-$third" },
                    fontSize = 20.sp
                )
            }
        }

        accountCreationDate.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadCreationDate)
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .padding(start = 12.dp, top = 12.dp)
                            .fillMaxHeight(0.1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
            }
            val exception = accountCreationDate.exceptionOrNull()
            val text: String = when (exception) {
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
                snackbarHostState.showSnackbar(text, retryText).let {
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
    pictureByteArray: Result<ByteArray>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        pictureByteArray == null -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                LoadingCircle()
            }
        }

        pictureByteArray.isSuccess -> {
            Image(
                bitmap = pictureByteArray.getOrThrow().decodeToImageBitmap(),
                contentDescription = "Profile icon",
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
        }

        pictureByteArray.isFailure -> {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadIcon)
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
            val exception = pictureByteArray.exceptionOrNull()
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
                snackbarHostState.showSnackbar(text, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onEvent(ViewAccountScreenEvent.ReloadIcon)
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
    accountName: Result<String>?,
    onEvent: (ViewAccountScreenEvent) -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    when {
        accountName == null -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                LoadingCircle()
            }
        }

        accountName.isSuccess -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(accountName.getOrThrow(), fontSize = 20.sp)
            }
        }

        accountName.isFailure -> {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .fillMaxHeight(0.75f)
                    .aspectRatio(1f)
                    .padding(start = 12.dp, top = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    onEvent(ViewAccountScreenEvent.ReloadName)
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
            val text: String = when (exception) {
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
                snackbarHostState.showSnackbar(text, retryText).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onEvent(ViewAccountScreenEvent.ReloadName)
                    }
                }
            }
        }
    }
}
