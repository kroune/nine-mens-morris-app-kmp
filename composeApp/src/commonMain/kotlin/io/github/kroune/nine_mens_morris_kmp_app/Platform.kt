package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
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
        throw it
    }.recoverNativeNetworkError(networkException)
}

expect fun <T> Result<T>.recoverNativeNetworkError(networkException: T): Result<T>
