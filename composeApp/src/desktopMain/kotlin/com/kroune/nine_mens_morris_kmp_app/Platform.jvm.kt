package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize
import java.io.IOException

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun getScreenIntSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}

actual fun <T> Result<T>.recoverNetworkError(networkException: Exception): Result<T> {
    if (networkException is IOException) {
        return Result.failure(networkException)
    }
    return this
}