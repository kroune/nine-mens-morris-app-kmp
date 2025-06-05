package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

actual fun <T> Result<T>.recoverNativeNetworkError(networkException: T): Result<T> {
    return recoverCatching {
        if (it is java.io.IOException || it is java.nio.channels.UnresolvedAddressException)
            return@recoverCatching networkException
        throw it
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun getScreenIntSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}
