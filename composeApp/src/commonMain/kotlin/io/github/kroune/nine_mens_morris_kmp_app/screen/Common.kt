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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import io.github.kroune.nine_mens_morris_kmp_app.model.AccountPictureByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.CreationDateByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.RatingByIdApiResponses
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
    text: @Composable (Long) -> Unit,
    accountRating: RatingByIdApiResponses?,
    reloadRating: () -> Unit,
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
            Modifier
                .size(120.dp, 30.dp)
                .clip(RoundedCornerShape(10))
        ) {
            when (it) {
                null -> {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .shimmerLoading(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                    }
                }

                is RatingByIdApiResponses.Success -> {
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        text(it.rating)
                    }
                }

                else -> {
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
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.CenterStart
                    ) {
                        IconButton(onClick = {
                            reloadRating()
                        }) {
                            Icon(
                                painter = painterResource(Res.drawable.error),
                                contentDescription = "Error",
                                modifier = Modifier
                                    .aspectRatio(1f, true)
                                    .clip(CircleShape)
                            )
                        }
                    }
                    var retryText = stringResource(Res.string.retry)
                    scope.launch {
                        snackbarHostState.showSnackbar(errorText, retryText)
                            .let { snackbarResult ->
                                if (snackbarResult == SnackbarResult.ActionPerformed) {
                                    reloadRating()
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
    text: @Composable (Triple<Int, Int, Int>) -> Unit,
    accountCreationDate: CreationDateByIdApiResponses?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    Box(
        Modifier
            .size(120.dp, 30.dp)
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
    pictureByteArray: AccountPictureByIdApiResponses?,
    onReload: () -> Unit,
    onClick: () -> Unit,
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
                .size(70.dp)
                .clip(CircleShape)
        ) {
            when (pictureByteArray) {
                null -> {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .aspectRatio(1f)
                            .shimmerLoading(),
                        contentAlignment = Alignment.Center
                    ) {
                    }
                }

                is AccountPictureByIdApiResponses.Success -> {
                    Image(
                        bitmap = pictureByteArray.picture.decodeToImageBitmap(),
                        contentDescription = "Profile icon",
                        modifier = Modifier
                            .clickable { onClick() }
                            .matchParentSize()
                            .aspectRatio(1f, true)
                    )
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
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
                                    .aspectRatio(1f)
                            )
                        }
                    }
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
    text: @Composable (String) -> Unit = @Composable {
        Text(it, fontSize = 20.sp)
    },
    accountName: LoginByIdApiResponses?,
    onReload: () -> Unit,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    Box(
        modifier
            .size(100.dp, 20.dp)
            .clip(RoundedCornerShape(10))
    ) {
        when (accountName) {
            null -> {
                Box(
                    Modifier
                        .matchParentSize()
                        .shimmerLoading(),
                    contentAlignment = Alignment.CenterStart
                ) {
                }
            }

            is LoginByIdApiResponses.Success -> {
                Box(
                    Modifier
                        .matchParentSize(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    text(accountName.login)
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .matchParentSize()
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
                                .aspectRatio(1f)
                                .clip(CircleShape)
                        )
                    }
                }
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
