package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.AccountPictureByIdApiResponses
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.retry
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
inline fun DrawIcon(
    modifier: Modifier = Modifier,
    crossinline onReload: () -> Unit,
    noinline onClick: () -> Unit,
    shape: Shape = CircleShape,
    crossinline onSuccess: @Composable BoxScope.(ImageBitmap) -> Unit = {
        Image(
            bitmap = it,
            contentDescription = "Profile icon",
            modifier = Modifier
                .matchParentSize()
                .clickable(onClick = onClick)
        )
    },
    pictureByteArray: AccountPictureByIdApiResponses<ImageBitmap>?,
    snackbarHostState: SnackbarHostState,
) {
    AnimatedContent(
        pictureByteArray,
        transitionSpec = {
            fadeIn(
                animationSpec = tween(220, 90)
            ) togetherWith fadeOut(animationSpec = tween(90))
        }
    ) {
        Box(
            modifier
                .clip(shape)
        ) {
            when (pictureByteArray) {
                null -> {
                    Box(
                        Modifier
                            .matchParentSize()
                            .shimmerLoading()
                    ) {
                    }
                }

                is AccountPictureByIdApiResponses.Success -> {
                    onSuccess(pictureByteArray.picture)
                }

                else -> {
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
                    LaunchedEffect(pictureByteArray) {
                        val text: String = when (pictureByteArray) {
                            is AccountPictureByIdApiResponses.NetworkError -> {
                                getString(Res.string.network_error)
                            }

                            is AccountPictureByIdApiResponses.UnknownError -> {
                                getString(Res.string.unknown_error)
                            }

                            is AccountPictureByIdApiResponses.CredentialsError -> {
                                getString(Res.string.credentials_error)
                            }

                            is AccountPictureByIdApiResponses.ServerError -> {
                                getString(Res.string.server_error)
                            }

                            is AccountPictureByIdApiResponses.Success -> return@LaunchedEffect
                        }
                        val retryText = getString(Res.string.retry)
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
