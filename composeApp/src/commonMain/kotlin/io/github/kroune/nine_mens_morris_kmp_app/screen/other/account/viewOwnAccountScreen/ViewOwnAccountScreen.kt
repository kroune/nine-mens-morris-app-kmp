package io.github.kroune.nine_mens_morris_kmp_app.screen.other.account.viewOwnAccountScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import io.github.kroune.nine_mens_morris_kmp_app.component.other.ViewOwnAccountScreenState
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.PastGamesApiResponse
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.event.other.ViewOwnAccountScreenEvent
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.RoundedCornerShape3
import io.github.kroune.nine_mens_morris_kmp_app.screen.UiConstants.padding2
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.CustomDrawRating
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawAccountCreationDate
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawIcon
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.DrawName
import io.github.kroune.nine_mens_morris_kmp_app.screen.other.account.DrawPlayedGameItem
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.log_out
import ninemensmorrisappkmp.composeapp.generated.resources.past_games_players
import ninemensmorrisappkmp.composeapp.generated.resources.rating
import ninemensmorrisappkmp.composeapp.generated.resources.upload_picture
import org.jetbrains.compose.resources.stringResource

@Composable
fun ViewOwnAccountScreen(
    state: ViewOwnAccountScreenState,
    onEvent: (ViewOwnAccountScreenEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = padding2),
                contentAlignment = Alignment.BottomCenter
            ) {
                Button(
                    onClick = {
                        onEvent(ViewOwnAccountScreenEvent.OnLogoutPressed)
                    },
                    shape = RoundedCornerShape3
                ) {
                    Text(stringResource(Res.string.log_out))
                }
            }
        }
    ) { padding ->
        BoxWithConstraints {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = padding2)
                    .padding(top = padding2),
                horizontalAlignment = Alignment.Start
            ) {
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        val size = min(
                            this@BoxWithConstraints.maxWidth,
                            this@BoxWithConstraints.maxHeight
                        ) / 2
                        DrawIcon(
                            Modifier
                                .size(size),
                            pictureByteArray = state.accountPictureResult,
                            onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadIcon) },
                            onClick = {},
                            snackbarHostState = snackbarHostState
                        )
                    }
                    DrawName(
                        modifier = Modifier
                            .clip(RoundedCornerShape3),
                        onSuccess = {
                            Text(
                                it,
                                fontSize = 30.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        accountName = state.accountLoginResult,
                        onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadName) },
                        placeholderStyle = TextStyle(
                            fontSize = 30.sp
                        ),
                        placeholderText = "some random name 123",
                        snackbarHostState = snackbarHostState
                    )
                }
                item {
                    CustomDrawRating(
                        modifier = Modifier,
                        onSuccess = {
                            Text(
                                "${stringResource(Res.string.rating)}: $it",
                                fontSize = 20.sp
                            )
                        },
                        placeholderText = "${stringResource(Res.string.rating)}: 12345",
                        accountRating = state.accountRatingResult,
                        onReload = { onEvent(ViewOwnAccountScreenEvent.ReloadRating) },
                        snackbarHostState = snackbarHostState
                    )
                }
                item {
                    DrawAccountCreationDate(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(30.dp),
                        onSuccess = { (first, second, third) ->
                            Text(
                                "$first.$second.$third",
                                fontSize = 20.sp
                            )
                        },
                        accountCreationDate = state.accountCreationDateResult,
                        onReload = {
                            onEvent(ViewOwnAccountScreenEvent.ReloadCreationDate)
                        },
                        placeholderText = "11.11.1111",
                        snackbarHostState = snackbarHostState
                    )
                }
                item {
                    val launcher = rememberFilePickerLauncher(
                        type = PickerType.Image,
                        mode = PickerMode.Single
                    ) { file ->
                        if (file == null) {
                            return@rememberFilePickerLauncher
                        }
                        scope.launch {
                            onEvent(ViewOwnAccountScreenEvent.UploadNewPicture(file.readBytes()))
                        }
                    }
                    Button(
                        { launcher.launch() },
                        shape = RoundedCornerShape3
                    ) {
                        Text(stringResource(Res.string.upload_picture))
                    }
                }

                item {
                    Card(
                        shape = UiConstants.RoundedCornerShape1
                    ) {
                        Row {
                            Text(
                                stringResource(Res.string.past_games_players),
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                    }
                }
                when (state.playedGamesList) {
                    is PastGamesApiResponse.CredentialsError -> TODO()
                    is PastGamesApiResponse.NetworkError -> TODO()
                    is PastGamesApiResponse.ServerError -> TODO()
                    is PastGamesApiResponse.Success -> {
                        items(
                            state.playedGamesList.playedGames
                        ) {
                            DrawPlayedGameItem(
                                it,
                                {
                                    onEvent(
                                        ViewOwnAccountScreenEvent.NavigateToViewPastGame(
                                            it.gameId
                                        )
                                    )
                                }
                            )
                        }
                    }

                    is PastGamesApiResponse.UnknownError -> TODO()
                    null -> {

                    }
                }
            }
            HandleOwnAccountScreenError(
                state.uploadingNewPictureResult,
                snackbarHostState
            )
        }
    }
}
