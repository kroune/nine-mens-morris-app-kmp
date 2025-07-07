package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.toSize
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.LoginByIdApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
import ninemensmorrisappkmp.composeapp.generated.resources.error
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.retry
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource

/**
 * draws user name or loading animation
 */
@Composable
inline fun DrawName(
    modifier: Modifier = Modifier,
    crossinline onReload: () -> Unit,
    crossinline onSuccess: @Composable BoxScope.(String) -> Unit = @Composable {
        Text(
            it,
            overflow = TextOverflow.Ellipsis
        )
    },
    placeholderStyle: TextStyle = TextStyle(),
    placeholderText: String,
    accountName: LoginByIdApiResponses?,
    snackbarHostState: SnackbarHostState,
) {
    val textMeasurer = rememberTextMeasurer(0)
    val textLayoutResult = textMeasurer.measure(
        placeholderText,
        style = placeholderStyle,
        skipCache = true
    )
    val placeholderSize =
        with(LocalDensity.current) { textLayoutResult.size.toSize().toDpSize() }
    Box(modifier) {
        when (accountName) {
            null -> {
                Box(
                    Modifier
                        .clip(RoundedCornerShape3)
                        .size(placeholderSize)
                        .shimmerLoading()
                ) {
                }
            }

            is LoginByIdApiResponses.Success -> {
                onSuccess(accountName.login)
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
                            .aspectRatio(1f)
                            .clip(CircleShape)
                    )
                }
                LaunchedEffect(accountName) {
                    val exceptionText: String = when (accountName) {
                        is LoginByIdApiResponses.Success -> return@LaunchedEffect

                        is LoginByIdApiResponses.NetworkError -> {
                            getString(Res.string.network_error)
                        }

                        is LoginByIdApiResponses.CredentialsError -> {
                            getString(Res.string.credentials_error)
                        }

                        is LoginByIdApiResponses.ServerError -> {
                            getString(Res.string.server_error)
                        }

                        is LoginByIdApiResponses.UnknownError -> {
                            getString(Res.string.unknown_error)
                        }
                    }
                    val retryText = getString(Res.string.retry)
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