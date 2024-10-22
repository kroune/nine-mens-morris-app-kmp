package com.kroune.nine_mens_morris_kmp_app.common

import androidx.compose.material.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import io.ktor.client.HttpClient


class TransparentColors : ButtonColors {
    @Composable
    override fun backgroundColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }

    @Composable
    override fun contentColor(enabled: Boolean): State<Color> {
        return mutableStateOf(Color.Transparent)
    }
}

val network = HttpClient()

/**
 * The server's address.
 * put your network ip here
 */
const val SERVER_ADDRESS = "://localhost:8080"//"://10.0.2.2:8080"

/**
 * The API endpoint for user-related operations.
 */
const val USER_API = "/api/v1/user"
