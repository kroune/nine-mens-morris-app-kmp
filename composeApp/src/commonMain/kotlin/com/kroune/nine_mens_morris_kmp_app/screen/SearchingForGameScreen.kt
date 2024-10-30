package com.kroune.nine_mens_morris_kmp_app.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.kroune.nine_mens_morris_kmp_app.component.SearchingForGameScreenComponent
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.game_expected_waiting_time
import ninemensmorrisappkmp.composeapp.generated.resources.searching_for_game
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchingForGameScreen(
    component: SearchingForGameScreenComponent
) {
    Column {
        Text(stringResource(Res.string.searching_for_game))
        // null = it is still loading
        if (component.expectedWaitingTime != null) {
            Text("${stringResource(Res.string.game_expected_waiting_time)} ${component.expectedWaitingTime}")
        }
    }
}