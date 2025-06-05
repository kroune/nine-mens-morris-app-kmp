package io.github.kroune.nine_mens_morris_kmp_app.screen.game.searchingForGameScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import io.github.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import kotlinx.coroutines.flow.receiveAsFlow
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_expected_waiting_time
import ninemensmorrisappkmp.composeapp.generated.resources.searching_for_game
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchingForGameScreen(
    component: SearchingForGameComponent
) {
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = stringResource(Res.string.searching_for_game),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                val waitingTime =
                    component.expectedWaitingTime.receiveAsFlow().collectAsState(null).value
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
        HandleSearchingForGameError(component.searchingForGameError.value, snackbarHostState)
    }
}
