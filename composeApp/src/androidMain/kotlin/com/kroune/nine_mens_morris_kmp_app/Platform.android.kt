package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun getScreenSize(): IntSize {
    return IntSize(
        LocalConfiguration.current.screenWidthDp,
        LocalConfiguration.current.screenHeightDp
    )
}

actual fun <T> Result<T>.recoverNetworkError(networkException: Exception): Result<T> {
    return recoverCatching {
        return when (it) {
            is ConnectException, is UnresolvedAddressException, is HttpRequestTimeoutException -> {
                Result.failure(networkException)
            }

            else -> {
                this
            }
        }
    }
}