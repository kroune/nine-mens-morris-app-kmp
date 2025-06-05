package io.github.kroune.nine_mens_morris_kmp_app.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.singUp.SignUpScreenState
import io.github.kroune.nine_mens_morris_kmp_app.model.event.auth.SignUpScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.model.api.AccountIdByJwtTokenApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.model.api.RegisterApiResponses
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.credentials_error
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
    component: SignUpScreenState,
    onEvent: (SignUpScreenEvent) -> Unit
) {
    val scope = rememberCoroutineScope()
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
                    Text(stringResource(Res.string.have_account_question_mark))
                    TextButton(
                        modifier = Modifier,
                        onClick = {
                            onEvent(SignUpScreenEvent.SwitchToSignInScreen)
                        },
                        colors = ExtendedColorTheme.colorScheme.linkColors
                    ) {
                        Text(stringResource(Res.string.sign_in))
                    }
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
                        .shadow(5.dp, RoundedCornerShape(15.dp)),
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
                    placeholder = { Text(stringResource(Res.string.login)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(resource = Res.drawable.username),
                            "your preferred username"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
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
                        .shadow(5.dp, RoundedCornerShape(15.dp)),
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
                    placeholder = { Text(stringResource(Res.string.password)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(resource = Res.drawable.password),
                            "your new password"
                        )
                    },
                    shape = RoundedCornerShape(15.dp),
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
                        .shadow(5.dp, RoundedCornerShape(15.dp)),
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
                    shape = RoundedCornerShape(15.dp),
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
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp, pressedElevation = 5.dp),
                    enabled = isUsernameValid && isPasswordValid &&
                            isPasswordRepeatedValid && !registrationInProcess,
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(stringResource(Res.string.sign_up))
                }
            }
        }
        HandleSignUpError(
            registrationResult,
            accountIdByJwtTokenResult,
            scope,
            snackbarHostState
        )
    }
}

@Composable
private fun HandleSignUpError(
    registrationResult: RegisterApiResponses?,
    accountIdByJwtTokenResult: AccountIdByJwtTokenApiResponses?,
    scope: CoroutineScope,
    snackbarHostState: SnackbarHostState
) {
    val text = when (registrationResult) {
        null -> {
            return
        }

        is RegisterApiResponses.Success -> {
            when (accountIdByJwtTokenResult) {
                is AccountIdByJwtTokenApiResponses.CredentialsError -> {
                    stringResource(Res.string.credentials_error)
                }

                is AccountIdByJwtTokenApiResponses.NetworkError -> {
                    stringResource(Res.string.network_error)
                }

                is AccountIdByJwtTokenApiResponses.ServerError -> {
                    stringResource(Res.string.server_error)
                }

                is AccountIdByJwtTokenApiResponses.UnknownError -> {
                    stringResource(Res.string.unknown_error)
                }

                is AccountIdByJwtTokenApiResponses.Success, null -> return
            }
        }

        is RegisterApiResponses.UnknownError -> {
            stringResource(Res.string.unknown_error)
        }

        is RegisterApiResponses.LoginAlreadyInUse -> {
            stringResource(Res.string.login_in_use)
        }

        is RegisterApiResponses.NetworkError -> {
            stringResource(Res.string.network_error)
        }

        is RegisterApiResponses.ServerError -> {
            stringResource(Res.string.server_error)
        }
    }
    scope.launch {
        snackbarHostState.showSnackbar(text)
    }
}
