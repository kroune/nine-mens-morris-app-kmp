package io.github.kroune.nine_mens_morris_kmp_app.screen.game.searchingForGameScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import io.github.kroune.nine_mens_morris_kmp_app.domain.entities.api.SearchingForGameResponse
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.LoadingCircle
import kotlinx.coroutines.flow.collectLatest
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_expected_waiting_time
import ninemensmorrisappkmp.composeapp.generated.resources.image_was_updated
import ninemensmorrisappkmp.composeapp.generated.resources.network_error
import ninemensmorrisappkmp.composeapp.generated.resources.searching_for_game
import ninemensmorrisappkmp.composeapp.generated.resources.server_error
import ninemensmorrisappkmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchingForGameScreen(
    component: SearchingForGameComponent
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(Res.string.searching_for_game),
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center,
            ) {
                val waitingTime =
                    component.expectedWaitingTime.collectAsState(null).value
                if (waitingTime == null) {
                    LoadingCircle()
                } else {
                    Text(
                        "${stringResource(Res.string.game_expected_waiting_time)} $waitingTime",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
        LaunchedEffect(Unit) {
            component.searchingForGameErrorFlow.collectLatest { forGameResponse ->
                val textRes = when (forGameResponse) {
                    is SearchingForGameResponse.Success -> Res.string.image_was_updated
                    is SearchingForGameResponse.ServerError -> Res.string.server_error
                    is SearchingForGameResponse.NetworkError -> Res.string.network_error
                    is SearchingForGameResponse.UnknownError -> Res.string.unknown_error
                }
                snackbarHostState.showSnackbar(getString(textRes))
            }
        }
    }
}
