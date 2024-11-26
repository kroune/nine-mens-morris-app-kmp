package com.kroune.nine_mens_morris_kmp_app.screen.game

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kroune.nine_mens_morris_kmp_app.common.BackHandler
import com.kroune.nine_mens_morris_kmp_app.component.game.SearchingForGameScreenComponent
import com.kroune.nine_mens_morris_kmp_app.event.SearchingForGameScreenEvent
import kotlinx.coroutines.flow.receiveAsFlow
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_expected_waiting_time
import ninemensmorrisappkmp.composeapp.generated.resources.searching_for_game
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchingForGameScreen(
    component: SearchingForGameScreenComponent
) {
    BackHandler(component.backHandler) {
        component.onEvent(SearchingForGameScreenEvent.Abort)
    }
    Column {
        Text(stringResource(Res.string.searching_for_game))
        Text(
            "${stringResource(Res.string.game_expected_waiting_time)} ${
                component.expectedWaitingTime.receiveAsFlow().collectAsState(null).value
            }"
        )
    }
}