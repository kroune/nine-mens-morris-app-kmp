package io.github.kroune.nine_mens_morris_kmp_app

import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.io.IOException

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
    onFailure {
        if (it is IOException) {
            lambda(it)
            return
        }
        if (it is ConnectTimeoutException) {
            lambda(it)
            return
        }
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
