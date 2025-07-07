package io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signUpScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.auth.SignUpScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.shadowElevation1
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.have_account_question_mark
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_login
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_password
import ninemensmorrisappkmp.composeapp.generated.resources.login
import ninemensmorrisappkmp.composeapp.generated.resources.passes_do_not_match
import ninemensmorrisappkmp.composeapp.generated.resources.password
import ninemensmorrisappkmp.composeapp.generated.resources.repeat_pass
import ninemensmorrisappkmp.composeapp.generated.resources.sign_in
import ninemensmorrisappkmp.composeapp.generated.resources.sign_up
import ninemensmorrisappkmp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    component: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    with(component) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            bottomBar = {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(Res.string.have_account_question_mark) + " ")
                    Text(
                        text = stringResource(Res.string.sign_in),
                        modifier = Modifier
                            .clip(UiConstants.RoundedCornerShape1)
                            .clickable(
                                onClick = {
                                    onEvent(SignUpScreenEvent.SwitchToSignInScreen)
                                }
                            ),
                        color = ExtendedColorTheme.colorScheme.linkColors
                    )
                }
            }
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    username,
                    { newValue ->
                        onEvent(SignUpScreenEvent.UpdateUsername(newValue))
                    },
                    modifier = Modifier
                        .shadow(shadowElevation1, RoundedCornerShape3),
                    label = {
                        if (!isUsernameValid && username.isNotEmpty()) {
                            Text(
                                stringResource(Res.string.invalid_login),
                                modifier = Modifier,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    },
                    placeholder = {
                        Text(stringResource(Res.string.login))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(resource = Res.drawable.username),
                            "your preferred username"
                        )
                    },
                    shape = RoundedCornerShape3,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.fillMaxHeight(0.025f))

                TextField(
                    password,
                    { newValue ->
                        onEvent(SignUpScreenEvent.UpdatePassword(newValue))
                    },
                    modifier = Modifier
                        .shadow(shadowElevation1, RoundedCornerShape3),
                    label = {
                        if (!isPasswordValid && password.isNotEmpty()) {
                            Text(
                                stringResource(Res.string.invalid_password),
                                modifier = Modifier,
                                color = Color.Red,
                                fontSize = 12.sp
                            )
                        }
                    },
                    placeholder = {
                        Text(stringResource(Res.string.password))
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(resource = Res.drawable.password),
                            "your new password"
                        )
                    },
                    shape = RoundedCornerShape3,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.fillMaxHeight(0.025f))

                TextField(
                    passwordRepeated,
                    { newValue ->
                        onEvent(SignUpScreenEvent.UpdateRepeatedPassword(newValue))
                    },
                    modifier = Modifier
                        .shadow(shadowElevation1, RoundedCornerShape3),
                    label = {
                        if (!isPasswordRepeatedValid && passwordRepeated.isNotEmpty()) {
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
                    },
                    shape = RoundedCornerShape3,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.fillMaxHeight(0.1f))
                Button(
                    onClick = {
                        onEvent(SignUpScreenEvent.Register)
                    },
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 10.dp,
                        pressedElevation = 5.dp
                    ),
                    enabled = isUsernameValid && isPasswordValid &&
                            isPasswordRepeatedValid && !registrationInProcess,
                    shape = RoundedCornerShape3
                ) {
                    Text(stringResource(Res.string.sign_up))
                }
            }
        }
        HandleSignUpError(
            registrationResult,
            accountIdByJwtTokenResult,
            snackbarHostState
        )
    }
}
