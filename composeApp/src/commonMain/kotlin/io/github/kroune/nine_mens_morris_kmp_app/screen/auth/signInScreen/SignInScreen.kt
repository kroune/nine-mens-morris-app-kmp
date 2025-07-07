package io.github.kroune.nine_mens_morris_kmp_app.screen.auth.signInScreen

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
import androidx.compose.material3.MaterialTheme
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
import io.github.kroune.nine_mens_morris_kmp_app.component.auth.signIn.SignInScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.auth.SignInScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.shadowElevation1
import io.github.kroune.nine_mens_morris_kmp_app.screen.theme.ExtendedColorTheme
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_login
import ninemensmorrisappkmp.composeapp.generated.resources.invalid_password
import ninemensmorrisappkmp.composeapp.generated.resources.login
import ninemensmorrisappkmp.composeapp.generated.resources.no_account_question_mark
import ninemensmorrisappkmp.composeapp.generated.resources.password
import ninemensmorrisappkmp.composeapp.generated.resources.sign_in
import ninemensmorrisappkmp.composeapp.generated.resources.sign_up
import ninemensmorrisappkmp.composeapp.generated.resources.username
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(
    state: SignInScreenState,
    onEvent: (SignInScreenEvent) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
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
                Text(stringResource(Res.string.no_account_question_mark) + " ")
                Text(
                    stringResource(Res.string.sign_up),
                    modifier = Modifier
                        .clip(UiConstants.RoundedCornerShape1)
                        .clickable(
                            onClick = {
                                onEvent(SignInScreenEvent.SwitchToSignInScreen)
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
                state.username,
                { newValue ->
                    onEvent(SignInScreenEvent.UsernameUpdate(newValue))
                },
                modifier = Modifier
                    .shadow(shadowElevation1, RoundedCornerShape3),
                isError = !state.isUsernameValid && state.username.isNotEmpty(),
                label = {
                    if (!state.isUsernameValid && state.username.isNotEmpty()) {
                        Text(
                            stringResource(Res.string.invalid_login),
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                },
                placeholder = { Text(stringResource(Res.string.login)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(resource = Res.drawable.username),
                        "your username"
                    )
                },
                shape = RoundedCornerShape3,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.025f))
            TextField(
                state.password,
                { newValue ->
                    onEvent(SignInScreenEvent.PasswordUpdate(newValue))
                },
                modifier = Modifier
                    .shadow(shadowElevation1, RoundedCornerShape3),
                isError = !state.isPasswordValid && state.password.isNotEmpty(),
                label = {
                    if (!state.isPasswordValid && state.password.isNotEmpty()) {
                        Text(
                            stringResource(Res.string.invalid_password),
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.error,
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
                        "your password"
                    )
                },
                shape = RoundedCornerShape3,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    onEvent(SignInScreenEvent.Login)
                },
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 5.dp
                ),
                enabled = state.isUsernameValid && state.isPasswordValid && !state.requestInProcess,
                shape = RoundedCornerShape3
            ) {
                Text(
                    stringResource(Res.string.sign_in)
                )
            }
        }
    }
    HandleSignInError(
        state.loginResult,
        state.accountIdByJwtTokenResult,
        snackbarHostState
    )
}
