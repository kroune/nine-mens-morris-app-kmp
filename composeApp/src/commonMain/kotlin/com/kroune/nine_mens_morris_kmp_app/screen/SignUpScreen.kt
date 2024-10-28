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
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.kroune.nine_mens_morris_kmp_app.component.SignUpScreenComponent
import com.kroune.nine_mens_morris_kmp_app.data.repository.source.remote.RegisterApiResponses
import com.kroune.nine_mens_morris_kmp_app.event.SignUpScreenEvent
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ClientErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.NetworkErrorPopUp
import com.kroune.nine_mens_morris_kmp_app.screen.popUps.ServerErrorPopUp
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.have_account_question_mark
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_login
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_password
import ninemensmorrisappkmp.composeapp.generated.resources.login
import ninemensmorrisappkmp.composeapp.generated.resources.login_in_use
import ninemensmorrisappkmp.composeapp.generated.resources.passes_do_not_match
import ninemensmorrisappkmp.composeapp.generated.resources.password
import ninemensmorrisappkmp.composeapp.generated.resources.repeat_pass
import ninemensmorrisappkmp.composeapp.generated.resources.sign_in
import ninemensmorrisappkmp.composeapp.generated.resources.sign_up
import ninemensmorrisappkmp.composeapp.generated.resources.switching_to_next_screen
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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (registrationResult != null) {
            val exception = registrationResult.exceptionOrNull()
            when (exception) {
                is RegisterApiResponses -> {
                    when (exception) {
                        RegisterApiResponses.ClientError -> {
                            ClientErrorPopUp()
                        }

                        RegisterApiResponses.LoginAlreadyInUse -> {
                            Text(
                                text = stringResource(Res.string.login_in_use),
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }

                        RegisterApiResponses.NetworkError -> {
                            NetworkErrorPopUp()
                        }

                        RegisterApiResponses.ServerError -> {
                            ServerErrorPopUp()
                        }
                    }
                }

                null -> {
                    Text(
                        text = stringResource(Res.string.switching_to_next_screen),
                        color = Color.Red,
                        fontSize = 12.sp
                    )
                }

                else -> {
                    ClientErrorPopUp()
                }
            }
        }
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
