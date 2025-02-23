package io.github.kroune.nine_mens_morris_kmp_app.screen.tutorial.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.kroune.nineMensMorrisLib.BLUE_
import com.kroune.nineMensMorrisLib.EMPTY
import com.kroune.nineMensMorrisLib.GREEN
import com.kroune.nineMensMorrisLib.Position
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.RenderGameBoard
import io.github.kroune.nine_mens_morris_kmp_app.screen.game.RenderPieceCount
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
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        BoxWithConstraints(contentAlignment = Alignment.TopCenter) {
            RenderPieceCount(
                pos = position
            )
            val heightBigger = derivedStateOf { maxHeight > maxWidth }
            RenderGameBoard(
                modifier = Modifier
                    // FIXME: this is some garbage
                    .then(
                        if (!heightBigger.value)
                            Modifier.fillMaxHeight(0.7f)
                        else
                            Modifier.fillMaxWidth(0.7f)
                    ),
                pos = position,
                selectedButton = 3,
                moveHints = listOf(),
                onClick = {}
            )
        }
        Text(
            text = stringResource(Res.string.tutorial_lose_condition),
            textAlign = TextAlign.Center
        )
    }
}
