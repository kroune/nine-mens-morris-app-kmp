package io.github.kroune.nine_mens_morris_kmp_app.screen.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.toSize
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.CreationDateByIdApiResponses
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
inline fun DrawAccountCreationDate(
    modifier: Modifier = Modifier,
    crossinline onReload: () -> Unit,
    onSuccess: @Composable (Triple<Int, Int, Int>) -> Unit = @Composable { (day, week, month) ->
        Text("$day.$week.$month")
    },
    placeholderStyle: TextStyle = TextStyle(),
    placeholderText: String,
    accountCreationDate: CreationDateByIdApiResponses?,
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
    Box(
        modifier
            .clip(RoundedCornerShape(10))
    ) {
        when (accountCreationDate) {
            null -> {
                Box(
                    Modifier
                        .size(placeholderSize)
                        .shimmerLoading()
                ) {
                }
            }

            is CreationDateByIdApiResponses.Success -> {
                onSuccess(accountCreationDate.creationDate)
            }

            else -> {
                IconButton(onClick = {
                    onReload()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.error),
                        contentDescription = "Error",
                        modifier = Modifier
                            .size(placeholderSize)
                            .clip(CircleShape)
                    )
                }
                LaunchedEffect(accountCreationDate) {
                    val exceptionText: String = when (accountCreationDate) {
                        is CreationDateByIdApiResponses.NetworkError -> {
                            getString(Res.string.network_error)
                        }

                        is CreationDateByIdApiResponses.UnknownError -> {
                            getString(Res.string.unknown_error)
                        }

                        is CreationDateByIdApiResponses.CredentialsError -> {
                            getString(Res.string.credentials_error)
                        }

                        is CreationDateByIdApiResponses.ServerError -> {
                            getString(Res.string.server_error)
                        }

                        is CreationDateByIdApiResponses.Success -> return@LaunchedEffect
                    }
                    val retryText = getString(Res.string.retry)
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