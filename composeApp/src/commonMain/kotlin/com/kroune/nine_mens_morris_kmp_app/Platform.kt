package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize

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

expect fun <T> Result<T>.recoverNetworkError(networkException: Exception): Result<T>