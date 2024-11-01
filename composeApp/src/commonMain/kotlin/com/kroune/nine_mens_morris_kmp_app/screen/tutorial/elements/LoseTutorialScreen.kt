package com.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import com.kroune.nine_mens_morris_kmp_app.common.GAME_BOARD_BUTTON_WIDTH
import com.kroune.nine_mens_morris_kmp_app.screen.RenderGameBoard
import com.kroune.nine_mens_morris_kmp_app.screen.RenderPieceCount
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_lose_condition
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells about lose conditions
 */
@Composable
fun RenderLoseTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  BLUE_,                  BLUE_,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  BLUE_,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  EMPTY,
                            EMPTY,  EMPTY,  EMPTY,
                    EMPTY,          EMPTY,          EMPTY,
            EMPTY,                  BLUE_,                  EMPTY
        ),
        // @formatter:on
        0u, 0u, pieceToMove = true, removalCount = 0u
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Box {
            RenderGameBoard(
                pos = position,
                selectedButton = 3,
                moveHints = listOf(),
                onClick = {}
            )
            RenderPieceCount(
                pos = position
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = GAME_BOARD_BUTTON_WIDTH,
                    end = GAME_BOARD_BUTTON_WIDTH
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.tutorial_lose_condition),
                textAlign = TextAlign.Center
            )
        }
    }
}
