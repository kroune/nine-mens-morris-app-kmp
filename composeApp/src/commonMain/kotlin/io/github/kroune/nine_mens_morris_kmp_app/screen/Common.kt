package io.github.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
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


fun ComponentContext.componentCoroutineScope(): CoroutineScope {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    if (lifecycle.state != Lifecycle.State.DESTROYED) {
        lifecycle.doOnDestroy {
            scope.cancel()
        }
    } else {
        scope.cancel()
    }
    return scope
}

/**
 * draws user rating
 */
@Composable
fun DrawRating(
    modifier: Modifier = Modifier
        .size(120.dp, 30.dp),
    onReload: () -> Unit,
    onSuccess: @Composable BoxScope.(Long) -> Unit = @Composable {
        Text(text = it.toString())
    },
    onLoading: @Composable BoxScope.() -> Unit = {
        Box(
            Modifier
                .matchParentSize()
                .shimmerLoading()
        ) {
        }
    },
    onFailure: @Composable BoxScope.() -> Unit = {
        IconButton(
            onClick = {
                onReload()
            },
            modifier = Modifier
                .matchParentSize()
        ) {
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = "Error",
                modifier = Modifier
            )
        }
    },
    accountRating: RatingByIdApiResponses?,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
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
        Box(
            modifier
                .clip(RoundedCornerShape(10))
        ) {
            when (it) {
                null -> {
                    onLoading()
                }

                is RatingByIdApiResponses.Success -> {
                    onSuccess(it.rating)
                }

                else -> {
                    onFailure()
                    val errorText = when (it) {
                        is RatingByIdApiResponses.NetworkError -> {
                            stringResource(Res.string.network_error)
                        }

                        RatingByIdApiResponses.UnknownError -> {
                            stringResource(Res.string.unknown_error)
                        }

                        RatingByIdApiResponses.CredentialsError -> {
                            stringResource(Res.string.credentials_error)
                        }

                        RatingByIdApiResponses.ServerError -> {
                            stringResource(Res.string.server_error)
                        }

                        is RatingByIdApiResponses.Success -> return@AnimatedContent
                    }
                    var retryText = stringResource(Res.string.retry)
                    scope.launch {
                        snackbarHostState.showSnackbar(errorText, retryText)
                            .let { snackbarResult ->
                                if (snackbarResult == SnackbarResult.ActionPerformed) {
                                    onReload()
                                }
                            }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawAccountCreationDate(
    modifier: Modifier = Modifier
        .size(120.dp, 30.dp),
    text: @Composable (Triple<Int, Int, Int>) -> Unit,
    accountCreationDate: CreationDateByIdApiResponses?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(10))
    ) {
        when (accountCreationDate) {
            null -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .shimmerLoading(),
                    contentAlignment = Alignment.CenterStart
                ) {
                }
            }

            is CreationDateByIdApiResponses.Success -> {
                text(accountCreationDate.creationDate)
            }

            else -> {
                IconButton(onClick = {
                    onReload()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .matchParentSize()
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }

                val exceptionText: String = when (accountCreationDate) {
                    is CreationDateByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    CreationDateByIdApiResponses.UnknownError -> {
                        stringResource(Res.string.unknown_error)
                    }

                    CreationDateByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    CreationDateByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    is CreationDateByIdApiResponses.Success -> return
                }
                val retryText = stringResource(Res.string.retry)
                scope.launch {
                    snackbarHostState.showSnackbar(exceptionText, retryText).let {
                        if (it == SnackbarResult.ActionPerformed) {
                            onReload()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Modifier.shimmerLoading(
    durationMillis: Int = 1000,
): Modifier {
    val transition = rememberInfiniteTransition(label = "")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )

    val shimmerColor = ExtendedColorTheme.colorScheme.shimmerColor
    return drawBehind {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    shimmerColor.copy(alpha = 0.2f),
                    shimmerColor.copy(alpha = 1.0f),
                    shimmerColor.copy(alpha = 0.2f),
                ),
                start = Offset(x = translateAnimation, y = translateAnimation),
                end = Offset(x = translateAnimation + 100f, y = translateAnimation + 100f),
            )
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawIcon(
    modifier: Modifier = Modifier,
    onReload: () -> Unit,
    onClick: () -> Unit,
    shape: Shape = CircleShape,
    onSuccess: @Composable BoxScope.(ByteArray) -> Unit = @Composable {
        Image(
            bitmap = it.decodeToImageBitmap(),
            contentDescription = "Profile icon",
            modifier = Modifier
                .clickable { onClick() }
                .matchParentSize()
                .aspectRatio(1f, true)
        )
    },
    onLoading: @Composable BoxScope.() -> Unit = {
        Box(
            Modifier
                .matchParentSize()
                .aspectRatio(1f, true)
                .shimmerLoading()
        ) {
        }
    },
    onFailure: @Composable BoxScope.() -> Unit = {
        IconButton(
            onClick = {
                onReload()
            },
            modifier = Modifier
                .matchParentSize()
                .aspectRatio(1f, true)
        ) {
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = "Error",
                modifier = Modifier
            )
        }
    },
    pictureByteArray: AccountPictureByIdApiResponses?,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    AnimatedContent(
        pictureByteArray,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(500)
            ) togetherWith fadeOut(animationSpec = tween(450))
        }
    ) {
        Box(
            modifier
                .clip(shape)
        ) {
            when (pictureByteArray) {
                null -> {
                    onLoading()
                }

                is AccountPictureByIdApiResponses.Success -> {
                    onSuccess(pictureByteArray.picture)
                }

                else -> {
                    onFailure()
                    val text: String = when (pictureByteArray) {
                        is AccountPictureByIdApiResponses.NetworkError -> {
                            stringResource(Res.string.network_error)
                        }

                        AccountPictureByIdApiResponses.UnknownError -> {
                            stringResource(Res.string.unknown_error)
                        }

                        AccountPictureByIdApiResponses.CredentialsError -> {
                            stringResource(Res.string.credentials_error)
                        }

                        AccountPictureByIdApiResponses.ServerError -> {
                            stringResource(Res.string.server_error)
                        }

                        is AccountPictureByIdApiResponses.Success -> return@AnimatedContent
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
}


/**
 * draws user name or loading animation
 */
@Composable
fun DrawName(
    modifier: Modifier = Modifier,
    onReload: () -> Unit,
    onSuccess: @Composable BoxScope.(String) -> Unit = @Composable {
        Text(
            it,
            overflow = TextOverflow.Ellipsis
        )
    },
    onLoading: @Composable BoxScope.() -> Unit = {
        Box(
            Modifier
                .clip(RoundedCornerShape(15.dp))
                .matchParentSize()
                .shimmerLoading()
        ) {
        }
    },
    onFailure: @Composable BoxScope.() -> Unit = {
        IconButton(onClick = {
            onReload()
        }) {
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = "Error",
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(CircleShape)
            )
        }
    },
    accountName: LoginByIdApiResponses?,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    Box(modifier) {
        when (accountName) {
            null -> {
                onLoading()
            }

            is LoginByIdApiResponses.Success -> {
                onSuccess(accountName.login)
            }

            else -> {
                onFailure()
                val exceptionText: String = when (accountName) {
                    is LoginByIdApiResponses.Success -> return

                    is LoginByIdApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    LoginByIdApiResponses.CredentialsError -> {
                        stringResource(Res.string.credentials_error)
                    }

                    LoginByIdApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }

                    LoginByIdApiResponses.UnknownError -> {
                        stringResource(Res.string.unknown_error)
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

@Composable
inline fun LimitSize(
    percentage: Float,
    crossinline content: @Composable BoxScope.() -> Unit,
) {
    BoxWithConstraints {
        Box(
            Modifier
                .sizeIn(
                    maxHeight = maxHeight * percentage,
                    maxWidth = maxWidth * percentage
                )
        ) {
            content()
        }
    }
}
