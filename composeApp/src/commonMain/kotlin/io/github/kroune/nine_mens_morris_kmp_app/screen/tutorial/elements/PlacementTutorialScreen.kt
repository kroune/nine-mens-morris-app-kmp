package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.common.LimitSize
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.gameBoardScreen.RenderPieceCount
import ninemensmorrisappkmp.composeapp.generated.resources.Res
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_placement_condition
import ninemensmorrisappkmp.composeapp.generated.resources.tutorial_placement_highlighting
import org.jetbrains.compose.resources.stringResource

/**
 * this screen tells how to perform placements
 */
@Composable
fun RenderPlacementTutorialScreen() {
    val position = Position(
        // @formatter:off
        arrayOf(
            BLUE_,                  BLUE_,                  GREEN,
                    GREEN,          EMPTY,          EMPTY,
                            EMPTY,  EMPTY,  EMPTY,
            EMPTY,  GREEN,  EMPTY,          EMPTY,  EMPTY,  BLUE_,
                            EMPTY,  GREEN,  EMPTY,
                    EMPTY,          BLUE_,          EMPTY,
            EMPTY,                  BLUE_,                  GREEN
        ),
        // @formatter:on
        1u, 2u, pieceToMove = false, removalCount = 0u
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        RenderPieceCount(
            pos = position
        )
        LimitSize(
            0.8f
        ) {
            RenderGameBoard(
                modifier = Modifier,
                pos = position,
                selectedButton = 3,
                moveHints = setOf(),
                onClick = {}
            )
        }
        Text(
            text = stringResource(Res.string.tutorial_placement_condition),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(Res.string.tutorial_placement_highlighting),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
    }
}
