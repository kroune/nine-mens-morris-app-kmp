package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntSize

@Composable
@OptIn(ExperimentalComposeUiApi::class)
actual fun getScreenSize(): IntSize {
    return LocalWindowInfo.current.containerSize
}

actual fun <T> Result<T>.recoverNetworkError(networkException: Exception): Result<T> {
    return this
}