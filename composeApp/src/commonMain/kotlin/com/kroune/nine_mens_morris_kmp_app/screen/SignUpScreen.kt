package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.kroune.nine_mens_morris_kmp_app.component.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RegisterApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.SignUpScreenEvent
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.client_error
import ninemensmorrisappkmp.composeapp.generated.resources.have_account_question_mark
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_login
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_password
import ninemensmorrisappkmp.composeapp.generated.resources.login
import ninemensmorrisappkmp.composeapp.generated.resources.login_in_use
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.passes_do_not_match
import ninemensmorrisappkmp.composeapp.generated.resources.password
import ninemensmorrisappkmp.composeapp.generated.resources.repeat_pass
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.sign_in
import ninemensmorrisappkmp.composeapp.generated.resources.sign_up
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import ninemensmorrisappkmp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    component: SignUpScreenComponent,
) {
    val username = component.username
    val isUsernameValid = component.usernameValid
    val password = component.password
    val isPasswordValid = component.passwordValid
    val passwordRepeated = component.passwordRepeated
    val isPasswordRepeatedValid = component.passwordRepeatedMatches
    val requestInProcess = component.registrationInProcess
    val registrationResult = component.registrationResult
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
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.fillMaxHeight(0.25f))
            TextField(
                username,
                { newValue ->
                    component.updateUsername(newValue)
                },
                label = {
                    if (!isUsernameValid) {
                        Text(
                            stringResource(Res.string.invalid_login),
                            modifier = Modifier,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                },
                placeholder = { Text(stringResource(Res.string.login)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.username),
                        "your preferred username"
                    )
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.025f))
            TextField(
                password,
                { newValue ->
                    component.updatePassword(newValue)
                },
                label = {
                    if (!isPasswordValid) {
                        Text(
                            stringResource(Res.string.invalid_password),
                            modifier = Modifier,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                },
                placeholder = { Text(stringResource(Res.string.password)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.password),
                        "your new password"
                    )
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.025f))
            TextField(
                passwordRepeated,
                { newValue ->
                    component.updatePasswordRepeated(newValue)
                },
                label = {
                    if (!isPasswordRepeatedValid) {
                        Text(
                            stringResource(Res.string.passes_do_not_match),
                            modifier = Modifier,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                },
                placeholder = { Text(stringResource(Res.string.repeat_pass)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.password),
                        stringResource(Res.string.repeat_pass)
                    )
                }
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    component.onEvent(SignUpScreenEvent.Register)
                },
                enabled = isUsernameValid && isPasswordValid &&
                        isPasswordRepeatedValid && !requestInProcess
            ) {
                Text(stringResource(Res.string.sign_up))
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(Res.string.have_account_question_mark))
                    TextButton(modifier = Modifier, onClick = {
                        component.onEvent(SignUpScreenEvent.SwitchToSignInScreen)
                    }) {
                        Text(stringResource(Res.string.sign_in))
                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
        }
    }

    if (registrationResult != null) {
        val exception = registrationResult.exceptionOrNull()
        val text = when (exception) {
            is RegisterApiResponses -> {
                when (exception) {
                    RegisterApiResponses.ClientError -> {
                        stringResource(Res.string.client_error)
                    }

                    RegisterApiResponses.LoginAlreadyInUse -> {
                        stringResource(Res.string.login_in_use)
                    }

                    RegisterApiResponses.NetworkError -> {
                        stringResource(Res.string.network_error)
                    }

                    RegisterApiResponses.ServerError -> {
                        stringResource(Res.string.server_error)
                    }
                }
            }

            else -> {
                stringResource(Res.string.unknown_error)
            }
        }
        scope.launch {
            snackbarHostState.showSnackbar(text)
        }
    }
}
