package com.kroune.nine_mens_morris_kmp_app

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize

@Composable
expect fun getScreenSize(): IntSize

expect fun <T> Result<T>.recoverNetworkError(networkException: Exception): Result<T>