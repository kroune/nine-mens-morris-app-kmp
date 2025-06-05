package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.io.IOException

@Composable
expect fun getScreenIntSize(): IntSize

@Composable
fun getScreenDpSize(): DpSize {
    with(
        LocalDensity.current
    ) {
        return DpSize(
            getScreenIntSize().width.toDp(),
            getScreenIntSize().height.toDp()
        )
    }
}

fun <T> Result<T>.recoverNetworkError(networkException: T): Result<T> {
    return recoverCatching {
        if (it is IOException)
            return@recoverCatching networkException
        if (it is ConnectTimeoutException)
            return@recoverCatching networkException
        throw it
    }.recoverNativeNetworkError(networkException)
}

inline fun <T> Result<T>.onNetworkError(lambda: (Throwable) -> Unit) {
    recoverCatching {
        if (it is IOException) {
            lambda(it)
            return@recoverCatching
        }
        if (it is ConnectTimeoutException) {
            lambda(it)
            return@recoverCatching
        }
        throw it
    }.onNativeNetworkError(
        lambda
    )
}

fun <T> Result<T>.recoverNativeNetworkError(networkException: T): Result<T> {
    onNetworkError {
        return Result.success(networkException)
    }
    return this
}

expect inline fun <T> Result<T>.onNativeNetworkError(lambda: (Throwable) -> Unit)
