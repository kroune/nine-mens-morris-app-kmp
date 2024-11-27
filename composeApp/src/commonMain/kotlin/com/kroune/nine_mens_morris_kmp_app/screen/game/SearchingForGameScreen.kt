package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kroune.nine_mens_morris_kmp_app.common.BackHandler
import com.kroune.nine_mens_morris_kmp_app.common.LoadingCircle
import com.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameComponent
import com.kroune.nine_mens_morris_kmp_app.event.SearchingForGameScreenEvent
import kotlinx.coroutines.flow.receiveAsFlow
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_expected_waiting_time
import ninemensmorrisappkmp.composeapp.generated.resources.searching_for_game
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchingForGameScreen(
    component: SearchingForGameComponent
) {
    BackHandler(component.backHandler) {
        component.onEvent(SearchingForGameScreenEvent.Back)
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column {
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(Res.string.searching_for_game)
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
                    "${stringResource(Res.string.game_expected_waiting_time)} $waitingTime"
                )
            }
        }
    }
}