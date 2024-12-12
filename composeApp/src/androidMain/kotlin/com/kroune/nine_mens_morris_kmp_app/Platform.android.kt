package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.net.ConnectException
import java.nio.channels.UnresolvedAddressException

@Composable
actual fun getScreenIntSize(): IntSize {
    with(LocalDensity.current) {
        return IntSize(
            LocalConfiguration.current.screenWidthDp.dp.roundToPx(),
            LocalConfiguration.current.screenHeightDp.dp.roundToPx()
        )
    }
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
