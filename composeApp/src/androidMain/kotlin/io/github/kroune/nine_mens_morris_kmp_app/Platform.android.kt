package io.github.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

@Composable
actual fun getScreenIntSize(): IntSize {
    with(LocalDensity.current) {
        return IntSize(
            LocalConfiguration.current.screenWidthDp.dp.roundToPx(),
            LocalConfiguration.current.screenHeightDp.dp.roundToPx()
        )
    }
}

actual fun <T> Result<T>.recoverNativeNetworkError(networkException: T): Result<T> {
    return recoverCatching {
        if (it is java.io.IOException || it is java.nio.channels.UnresolvedAddressException)
            return@recoverCatching networkException
        throw it
    }
}