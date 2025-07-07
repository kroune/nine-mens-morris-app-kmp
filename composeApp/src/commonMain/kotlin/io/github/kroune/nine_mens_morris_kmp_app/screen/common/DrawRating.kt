package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.toSize
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.RatingByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.retry
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource

@Composable
inline fun CustomDrawRating(
    modifier: Modifier = Modifier,
    accountRating: RatingByIdApiResponses?,
    crossinline onReload: () -> Unit,
    crossinline onSuccess: @Composable BoxScope.(Long) -> Unit = @Composable {
        Text(text = it.toString(), overflow = TextOverflow.Ellipsis)
    },
    placeholderStyle: TextStyle = TextStyle(),
    placeholderText: String,
    snackbarHostState: SnackbarHostState
) {
    val textMeasurer = rememberTextMeasurer(0)
    val textLayoutResult = textMeasurer.measure(
        placeholderText,
        style = placeholderStyle,
        skipCache = true
    )
    val placeholderSize =
        with(LocalDensity.current) { textLayoutResult.size.toSize().toDpSize() }
    AnimatedContent(
        targetState = accountRating,
        transitionSpec = {
            (fadeIn(animationSpec = tween(220, delayMillis = 90)))
                .togetherWith(fadeOut(animationSpec = tween(90)))
        },
        modifier = Modifier
            .clip(UiConstants.RoundedCornerShape2),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier
        ) {
            when (it) {
                null -> {
                    Box(
                        Modifier
                            .size(placeholderSize)
                            .shimmerLoading()
                    ) {
                    }
                }

                is RatingByIdApiResponses.Success -> {
                    onSuccess(it.rating)
                }

                else -> {
                    IconButton(
                        onClick = {
                            onReload()
                        },
                        modifier = Modifier
                            .size(placeholderSize)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            modifier = Modifier
                        )
                    }
                    LaunchedEffect(it) {
                        val errorText = when (it) {
                            is RatingByIdApiResponses.NetworkError -> {
                                getString(Res.string.network_error)
                            }

                            is RatingByIdApiResponses.UnknownError -> {
                                getString(Res.string.unknown_error)
                            }

                            is RatingByIdApiResponses.CredentialsError -> {
                                getString(Res.string.credentials_error)
                            }

                            is RatingByIdApiResponses.ServerError -> {
                                getString(Res.string.server_error)
                            }

                            is RatingByIdApiResponses.Success -> return@LaunchedEffect
                        }
                        val retryText = getString(Res.string.retry)
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