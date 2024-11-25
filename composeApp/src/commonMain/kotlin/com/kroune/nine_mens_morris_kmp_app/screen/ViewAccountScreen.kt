package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kroune.nine_mens_morris_kmp_app.common.DrawAccountCreationDate
import com.kroune.nine_mens_morris_kmp_app.common.DrawIcon
import com.kroune.nine_mens_morris_kmp_app.common.DrawName
import com.kroune.nine_mens_morris_kmp_app.common.DrawRating
import com.kroune.nine_mens_morris_kmp_app.component.ViewAccountScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.ViewAccountScreenEvent

@Composable
fun ViewAccountScreen(
    component: ViewAccountScreenComponent
) {
    val isOwnAccount = component.isOwnAccount
    val name = component.accountName
    val rating = component.accountRating
    val creationDate = component.accountCreationDate
    val picture = component.accountPicture
    val onEvent: (ViewAccountScreenEvent) -> Unit = { component.onEvent(it) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        backgroundColor = Color.Transparent
    ) { contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.175f)
                    .fillMaxWidth()
            ) {
                DrawIcon(
                    pictureByteArray = picture,
                    onReload = { onEvent(ViewAccountScreenEvent.ReloadIcon) },
                    scope = scope,
                    snackbarHostState = snackbarHostState
                )
                DrawName(
                    {
                        it
                    },
                    name,
                    { onEvent(ViewAccountScreenEvent.ReloadName) },
                    scope,
                    snackbarHostState
                )
            }
            DrawRating(
                text = {
                    Text("User rating is $it")
                },
                accountRating = rating,
                reloadRating = { onEvent(ViewAccountScreenEvent.ReloadRating) },
                scope = scope,
                snackbarHostState = snackbarHostState
            )
            DrawAccountCreationDate(
                { (first, second, third) -> "$first-$second-$third" },
                creationDate, onEvent, scope, snackbarHostState
            )
            if (isOwnAccount) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    DrawOwnAccountOptions(
                        onEvent
                    )
                }
            }
        }
    }
}

/**
 * draws specific settings for our account
 */
@Composable
fun DrawOwnAccountOptions(
    onEvent: (ViewAccountScreenEvent) -> Unit
) {
    Button(
        onClick = {
            onEvent(ViewAccountScreenEvent.Logout)
        }
    ) {
        Text("Log out")
    }
}
